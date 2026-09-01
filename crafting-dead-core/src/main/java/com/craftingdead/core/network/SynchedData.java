/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between
 * you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be
 * bound by the terms and conditions of this Agreement as may be revised from time
 * to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download,
 * copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.core.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import com.google.common.collect.Lists;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;

public class SynchedData {

  private final Map<Integer, DataEntry<?>> entries = new Int2ObjectOpenHashMap<>();
  @Nullable
  private final Runnable dirtyListener;
  private boolean empty = true;
  private boolean dirty;

  public SynchedData() {
    this(null);
  }

  public SynchedData(@Nullable Runnable dirtyListener) {
    this.dirtyListener = dirtyListener;
  }

  public <T> void register(EntityDataAccessor<T> parameter, T value) {
    int id = parameter.id();
    if (id > 254) {
      throw new IllegalArgumentException(
          "Data parameter id is too big with " + id + "! (Max is 254)");
    } else if (this.entries.containsKey(id)) {
      throw new IllegalArgumentException("Duplicate id value for " + id + "!");
    } else if (EntityDataSerializers.getSerializedId(parameter.serializer()) < 0) {
      throw new IllegalArgumentException(
          "Unregistered serializer " + parameter.serializer() + " for " + id + "!");
    } else {
      this.<T>createEntry(parameter, value);
    }
  }

  private <T> void createEntry(EntityDataAccessor<T> parameter, T value) {
    DataEntry<T> entry = new DataEntry<>(parameter, value);
    this.entries.put(parameter.id(), entry);
    this.empty = false;
  }

  @SuppressWarnings("unchecked")
  private <T> DataEntry<T> getEntry(EntityDataAccessor<T> parameter) {
    DataEntry<T> entry;
    try {
      entry = (DataEntry<T>) this.entries.get(parameter.id());
    } catch (Throwable throwable) {
      CrashReport crashReport =
          CrashReport.forThrowable(throwable, "Getting data entry");
      CrashReportCategory category = crashReport.addCategory("Getting data entry");
      category.setDetail("Data parameter ID", parameter);
      throw new ReportedException(crashReport);
    }

    return entry;
  }

  public <T> T get(EntityDataAccessor<T> parameter) {
    return this.getEntry(parameter).getValue();
  }

  public <T> void set(EntityDataAccessor<T> parameter, T value) {
    DataEntry<T> entry = this.<T>getEntry(parameter);
    if (ObjectUtils.notEqual(value, entry.getValue())) {
      entry.setValue(value);
      entry.setDirty(true);
      this.markDirty();
    }
  }

  public <T> T compute(EntityDataAccessor<T> parameter, Function<T, T> remappingFunction) {
    DataEntry<T> entry = this.<T>getEntry(parameter);
    T newValue = remappingFunction.apply(entry.getValue());
    if (ObjectUtils.notEqual(newValue, entry.getValue())) {
      entry.setValue(newValue);
      entry.setDirty(true);
      this.markDirty();
    }
    return newValue;
  }

  public boolean isDirty() {
    return this.dirty;
  }

  private void markDirty() {
    if (!this.dirty && this.dirtyListener != null) {
      this.dirtyListener.run();
    }
    this.dirty = true;
  }

  public static void pack(List<DataEntry<?>> entries, FriendlyByteBuf buf) {
    if (entries != null) {
      for (DataEntry<?> entry : entries) {
        writeEntry(buf, entry);
      }
    }
    buf.writeByte(255);
  }

  private static <T> void writeEntry(FriendlyByteBuf out, DataEntry<T> entry) {
    EntityDataAccessor<T> parameter = entry.getKey();
    int i = EntityDataSerializers.getSerializedId(parameter.serializer());
    if (i < 0) {
      throw new EncoderException("Unknown serializer type " + parameter.serializer());
    } else {
      out.writeByte(parameter.id());
      out.writeVarInt(i);
      parameter.serializer().codec().encode((RegistryFriendlyByteBuf) out, entry.getValue());
    }
  }

  @Nullable
  public List<DataEntry<?>> packDirty() {
    List<DataEntry<?>> list = null;
    if (this.dirty) {
      for (DataEntry<?> entry : this.entries.values()) {
        if (entry.isDirty()) {
          entry.setDirty(false);
          if (list == null) {
            list = Lists.newArrayList();
          }
          list.add(entry.copy());
        }
      }
    }
    this.dirty = false;
    return list;
  }

  @Nullable
  public List<DataEntry<?>> getAll() {
    List<DataEntry<?>> list = null;
    for (DataEntry<?> entry : this.entries.values()) {
      if (list == null) {
        list = new ArrayList<>();
      }
      list.add(entry.copy());
    }
    return list;
  }

  @Nullable
  public static List<DataEntry<?>> unpack(FriendlyByteBuf buf) {
    List<DataEntry<?>> entries = null;

    int id;
    while ((id = buf.readUnsignedByte()) != 255) {
      if (entries == null) {
        entries = Lists.newArrayList();
      }

      int j = buf.readVarInt();
      EntityDataSerializer<?> serializer = EntityDataSerializers.getSerializer(j);
      if (serializer == null) {
        throw new DecoderException("Unknown serializer type " + j);
      }

      entries.add(readEntry(buf, id, serializer));
    }

    return entries;
  }

  private static <T> SynchedData.DataEntry<T> readEntry(FriendlyByteBuf buf,
      int id, EntityDataSerializer<T> serializer) {
    return new SynchedData.DataEntry<>(serializer.createAccessor(id),
        serializer.codec().decode((RegistryFriendlyByteBuf) buf));
  }

  public void assignValues(@Nullable List<DataEntry<?>> entries) {
    if (entries == null) {
      return;
    }

    for (DataEntry<?> entry : entries) {
      DataEntry<?> currentEntry = this.entries.get(entry.getKey().id());
      if (currentEntry != null) {
        this.assignValue(currentEntry, entry);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private <T> void assignValue(DataEntry<T> destination, DataEntry<?> source) {
    if (!Objects.equals(source.parameter.serializer(), destination.parameter.serializer())) {
      throw new IllegalStateException(String.format(
          "Data entry mismatch for %d: old=%s(%s), new=%s(%s)",
          destination.parameter.id(), destination.value, destination.value.getClass(),
          source.value,
          source.value.getClass()));
    } else {
      destination.setValue((T) source.getValue());
    }
  }

  public boolean isEmpty() {
    return this.empty;
  }

  public void cleanDirty() {
    this.dirty = false;
    for (DataEntry<?> entry : this.entries.values()) {
      entry.setDirty(false);
    }
  }

  public static class DataEntry<T> {

    private final EntityDataAccessor<T> parameter;
    private T value;
    private boolean dirty;

    private DataEntry(EntityDataAccessor<T> parameter, T value) {
      this.parameter = parameter;
      this.value = value;
      this.dirty = true;
    }

    public EntityDataAccessor<T> getKey() {
      return this.parameter;
    }

    public void setValue(T valueIn) {
      this.value = valueIn;
    }

    public T getValue() {
      return this.value;
    }

    public boolean isDirty() {
      return this.dirty;
    }

    public void setDirty(boolean dirty) {
      this.dirty = dirty;
    }

    public SynchedData.DataEntry<T> copy() {
      return new SynchedData.DataEntry<>(this.parameter,
          this.parameter.serializer().copy(this.value));
    }
  }
}
