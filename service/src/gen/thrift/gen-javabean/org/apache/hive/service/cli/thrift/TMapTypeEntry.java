/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.hive.service.cli.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2017-10-11")
public class TMapTypeEntry implements org.apache.thrift.TBase<TMapTypeEntry, TMapTypeEntry._Fields>, java.io.Serializable, Cloneable, Comparable<TMapTypeEntry> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TMapTypeEntry");

  private static final org.apache.thrift.protocol.TField KEY_TYPE_PTR_FIELD_DESC = new org.apache.thrift.protocol.TField("keyTypePtr", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField VALUE_TYPE_PTR_FIELD_DESC = new org.apache.thrift.protocol.TField("valueTypePtr", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TMapTypeEntryStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TMapTypeEntryTupleSchemeFactory());
  }

  private int keyTypePtr; // required
  private int valueTypePtr; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    KEY_TYPE_PTR((short)1, "keyTypePtr"),
    VALUE_TYPE_PTR((short)2, "valueTypePtr");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // KEY_TYPE_PTR
          return KEY_TYPE_PTR;
        case 2: // VALUE_TYPE_PTR
          return VALUE_TYPE_PTR;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __KEYTYPEPTR_ISSET_ID = 0;
  private static final int __VALUETYPEPTR_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.KEY_TYPE_PTR, new org.apache.thrift.meta_data.FieldMetaData("keyTypePtr", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "TTypeEntryPtr")));
    tmpMap.put(_Fields.VALUE_TYPE_PTR, new org.apache.thrift.meta_data.FieldMetaData("valueTypePtr", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "TTypeEntryPtr")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TMapTypeEntry.class, metaDataMap);
  }

  public TMapTypeEntry() {
  }

  public TMapTypeEntry(
    int keyTypePtr,
    int valueTypePtr)
  {
    this();
    this.keyTypePtr = keyTypePtr;
    setKeyTypePtrIsSet(true);
    this.valueTypePtr = valueTypePtr;
    setValueTypePtrIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TMapTypeEntry(TMapTypeEntry other) {
    __isset_bitfield = other.__isset_bitfield;
    this.keyTypePtr = other.keyTypePtr;
    this.valueTypePtr = other.valueTypePtr;
  }

  public TMapTypeEntry deepCopy() {
    return new TMapTypeEntry(this);
  }

  @Override
  public void clear() {
    setKeyTypePtrIsSet(false);
    this.keyTypePtr = 0;
    setValueTypePtrIsSet(false);
    this.valueTypePtr = 0;
  }

  public int getKeyTypePtr() {
    return this.keyTypePtr;
  }

  public void setKeyTypePtr(int keyTypePtr) {
    this.keyTypePtr = keyTypePtr;
    setKeyTypePtrIsSet(true);
  }

  public void unsetKeyTypePtr() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __KEYTYPEPTR_ISSET_ID);
  }

  /** Returns true if field keyTypePtr is set (has been assigned a value) and false otherwise */
  public boolean isSetKeyTypePtr() {
    return EncodingUtils.testBit(__isset_bitfield, __KEYTYPEPTR_ISSET_ID);
  }

  public void setKeyTypePtrIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __KEYTYPEPTR_ISSET_ID, value);
  }

  public int getValueTypePtr() {
    return this.valueTypePtr;
  }

  public void setValueTypePtr(int valueTypePtr) {
    this.valueTypePtr = valueTypePtr;
    setValueTypePtrIsSet(true);
  }

  public void unsetValueTypePtr() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __VALUETYPEPTR_ISSET_ID);
  }

  /** Returns true if field valueTypePtr is set (has been assigned a value) and false otherwise */
  public boolean isSetValueTypePtr() {
    return EncodingUtils.testBit(__isset_bitfield, __VALUETYPEPTR_ISSET_ID);
  }

  public void setValueTypePtrIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __VALUETYPEPTR_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case KEY_TYPE_PTR:
      if (value == null) {
        unsetKeyTypePtr();
      } else {
        setKeyTypePtr((Integer)value);
      }
      break;

    case VALUE_TYPE_PTR:
      if (value == null) {
        unsetValueTypePtr();
      } else {
        setValueTypePtr((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case KEY_TYPE_PTR:
      return Integer.valueOf(getKeyTypePtr());

    case VALUE_TYPE_PTR:
      return Integer.valueOf(getValueTypePtr());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case KEY_TYPE_PTR:
      return isSetKeyTypePtr();
    case VALUE_TYPE_PTR:
      return isSetValueTypePtr();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TMapTypeEntry)
      return this.equals((TMapTypeEntry)that);
    return false;
  }

  public boolean equals(TMapTypeEntry that) {
    if (that == null)
      return false;

    boolean this_present_keyTypePtr = true;
    boolean that_present_keyTypePtr = true;
    if (this_present_keyTypePtr || that_present_keyTypePtr) {
      if (!(this_present_keyTypePtr && that_present_keyTypePtr))
        return false;
      if (this.keyTypePtr != that.keyTypePtr)
        return false;
    }

    boolean this_present_valueTypePtr = true;
    boolean that_present_valueTypePtr = true;
    if (this_present_valueTypePtr || that_present_valueTypePtr) {
      if (!(this_present_valueTypePtr && that_present_valueTypePtr))
        return false;
      if (this.valueTypePtr != that.valueTypePtr)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_keyTypePtr = true;
    list.add(present_keyTypePtr);
    if (present_keyTypePtr)
      list.add(keyTypePtr);

    boolean present_valueTypePtr = true;
    list.add(present_valueTypePtr);
    if (present_valueTypePtr)
      list.add(valueTypePtr);

    return list.hashCode();
  }

  @Override
  public int compareTo(TMapTypeEntry other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetKeyTypePtr()).compareTo(other.isSetKeyTypePtr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetKeyTypePtr()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.keyTypePtr, other.keyTypePtr);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetValueTypePtr()).compareTo(other.isSetValueTypePtr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetValueTypePtr()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.valueTypePtr, other.valueTypePtr);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TMapTypeEntry(");
    boolean first = true;

    sb.append("keyTypePtr:");
    sb.append(this.keyTypePtr);
    first = false;
    if (!first) sb.append(", ");
    sb.append("valueTypePtr:");
    sb.append(this.valueTypePtr);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetKeyTypePtr()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'keyTypePtr' is unset! Struct:" + toString());
    }

    if (!isSetValueTypePtr()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'valueTypePtr' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TMapTypeEntryStandardSchemeFactory implements SchemeFactory {
    public TMapTypeEntryStandardScheme getScheme() {
      return new TMapTypeEntryStandardScheme();
    }
  }

  private static class TMapTypeEntryStandardScheme extends StandardScheme<TMapTypeEntry> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TMapTypeEntry struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // KEY_TYPE_PTR
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.keyTypePtr = iprot.readI32();
              struct.setKeyTypePtrIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // VALUE_TYPE_PTR
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.valueTypePtr = iprot.readI32();
              struct.setValueTypePtrIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TMapTypeEntry struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(KEY_TYPE_PTR_FIELD_DESC);
      oprot.writeI32(struct.keyTypePtr);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(VALUE_TYPE_PTR_FIELD_DESC);
      oprot.writeI32(struct.valueTypePtr);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TMapTypeEntryTupleSchemeFactory implements SchemeFactory {
    public TMapTypeEntryTupleScheme getScheme() {
      return new TMapTypeEntryTupleScheme();
    }
  }

  private static class TMapTypeEntryTupleScheme extends TupleScheme<TMapTypeEntry> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TMapTypeEntry struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.keyTypePtr);
      oprot.writeI32(struct.valueTypePtr);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TMapTypeEntry struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.keyTypePtr = iprot.readI32();
      struct.setKeyTypePtrIsSet(true);
      struct.valueTypePtr = iprot.readI32();
      struct.setValueTypePtrIsSet(true);
    }
  }

}

