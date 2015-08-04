/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.hadoop.hive.metastore.api;

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
public class DropPartitionsRequest implements org.apache.thrift.TBase<DropPartitionsRequest, DropPartitionsRequest._Fields>, java.io.Serializable, Cloneable, Comparable<DropPartitionsRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DropPartitionsRequest");

  private static final org.apache.thrift.protocol.TField DB_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("dbName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TBL_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("tblName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField PARTS_FIELD_DESC = new org.apache.thrift.protocol.TField("parts", org.apache.thrift.protocol.TType.STRUCT, (short)3);
  private static final org.apache.thrift.protocol.TField DELETE_DATA_FIELD_DESC = new org.apache.thrift.protocol.TField("deleteData", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField IF_EXISTS_FIELD_DESC = new org.apache.thrift.protocol.TField("ifExists", org.apache.thrift.protocol.TType.BOOL, (short)5);
  private static final org.apache.thrift.protocol.TField IGNORE_PROTECTION_FIELD_DESC = new org.apache.thrift.protocol.TField("ignoreProtection", org.apache.thrift.protocol.TType.BOOL, (short)6);
  private static final org.apache.thrift.protocol.TField ENVIRONMENT_CONTEXT_FIELD_DESC = new org.apache.thrift.protocol.TField("environmentContext", org.apache.thrift.protocol.TType.STRUCT, (short)7);
  private static final org.apache.thrift.protocol.TField NEED_RESULT_FIELD_DESC = new org.apache.thrift.protocol.TField("needResult", org.apache.thrift.protocol.TType.BOOL, (short)8);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new DropPartitionsRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new DropPartitionsRequestTupleSchemeFactory());
  }

  private String dbName; // required
  private String tblName; // required
  private RequestPartsSpec parts; // required
  private boolean deleteData; // optional
  private boolean ifExists; // optional
  private boolean ignoreProtection; // optional
  private EnvironmentContext environmentContext; // optional
  private boolean needResult; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    DB_NAME((short)1, "dbName"),
    TBL_NAME((short)2, "tblName"),
    PARTS((short)3, "parts"),
    DELETE_DATA((short)4, "deleteData"),
    IF_EXISTS((short)5, "ifExists"),
    IGNORE_PROTECTION((short)6, "ignoreProtection"),
    ENVIRONMENT_CONTEXT((short)7, "environmentContext"),
    NEED_RESULT((short)8, "needResult");

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
        case 1: // DB_NAME
          return DB_NAME;
        case 2: // TBL_NAME
          return TBL_NAME;
        case 3: // PARTS
          return PARTS;
        case 4: // DELETE_DATA
          return DELETE_DATA;
        case 5: // IF_EXISTS
          return IF_EXISTS;
        case 6: // IGNORE_PROTECTION
          return IGNORE_PROTECTION;
        case 7: // ENVIRONMENT_CONTEXT
          return ENVIRONMENT_CONTEXT;
        case 8: // NEED_RESULT
          return NEED_RESULT;
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
  private static final int __DELETEDATA_ISSET_ID = 0;
  private static final int __IFEXISTS_ISSET_ID = 1;
  private static final int __IGNOREPROTECTION_ISSET_ID = 2;
  private static final int __NEEDRESULT_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.DELETE_DATA,_Fields.IF_EXISTS,_Fields.IGNORE_PROTECTION,_Fields.ENVIRONMENT_CONTEXT,_Fields.NEED_RESULT};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DB_NAME, new org.apache.thrift.meta_data.FieldMetaData("dbName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TBL_NAME, new org.apache.thrift.meta_data.FieldMetaData("tblName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PARTS, new org.apache.thrift.meta_data.FieldMetaData("parts", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RequestPartsSpec.class)));
    tmpMap.put(_Fields.DELETE_DATA, new org.apache.thrift.meta_data.FieldMetaData("deleteData", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.IF_EXISTS, new org.apache.thrift.meta_data.FieldMetaData("ifExists", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.IGNORE_PROTECTION, new org.apache.thrift.meta_data.FieldMetaData("ignoreProtection", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.ENVIRONMENT_CONTEXT, new org.apache.thrift.meta_data.FieldMetaData("environmentContext", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, EnvironmentContext.class)));
    tmpMap.put(_Fields.NEED_RESULT, new org.apache.thrift.meta_data.FieldMetaData("needResult", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DropPartitionsRequest.class, metaDataMap);
  }

  public DropPartitionsRequest() {
    this.ifExists = true;

    this.needResult = true;

  }

  public DropPartitionsRequest(
    String dbName,
    String tblName,
    RequestPartsSpec parts)
  {
    this();
    this.dbName = dbName;
    this.tblName = tblName;
    this.parts = parts;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DropPartitionsRequest(DropPartitionsRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetDbName()) {
      this.dbName = other.dbName;
    }
    if (other.isSetTblName()) {
      this.tblName = other.tblName;
    }
    if (other.isSetParts()) {
      this.parts = new RequestPartsSpec(other.parts);
    }
    this.deleteData = other.deleteData;
    this.ifExists = other.ifExists;
    this.ignoreProtection = other.ignoreProtection;
    if (other.isSetEnvironmentContext()) {
      this.environmentContext = new EnvironmentContext(other.environmentContext);
    }
    this.needResult = other.needResult;
  }

  public DropPartitionsRequest deepCopy() {
    return new DropPartitionsRequest(this);
  }

  @Override
  public void clear() {
    this.dbName = null;
    this.tblName = null;
    this.parts = null;
    setDeleteDataIsSet(false);
    this.deleteData = false;
    this.ifExists = true;

    setIgnoreProtectionIsSet(false);
    this.ignoreProtection = false;
    this.environmentContext = null;
    this.needResult = true;

  }

  public String getDbName() {
    return this.dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public void unsetDbName() {
    this.dbName = null;
  }

  /** Returns true if field dbName is set (has been assigned a value) and false otherwise */
  public boolean isSetDbName() {
    return this.dbName != null;
  }

  public void setDbNameIsSet(boolean value) {
    if (!value) {
      this.dbName = null;
    }
  }

  public String getTblName() {
    return this.tblName;
  }

  public void setTblName(String tblName) {
    this.tblName = tblName;
  }

  public void unsetTblName() {
    this.tblName = null;
  }

  /** Returns true if field tblName is set (has been assigned a value) and false otherwise */
  public boolean isSetTblName() {
    return this.tblName != null;
  }

  public void setTblNameIsSet(boolean value) {
    if (!value) {
      this.tblName = null;
    }
  }

  public RequestPartsSpec getParts() {
    return this.parts;
  }

  public void setParts(RequestPartsSpec parts) {
    this.parts = parts;
  }

  public void unsetParts() {
    this.parts = null;
  }

  /** Returns true if field parts is set (has been assigned a value) and false otherwise */
  public boolean isSetParts() {
    return this.parts != null;
  }

  public void setPartsIsSet(boolean value) {
    if (!value) {
      this.parts = null;
    }
  }

  public boolean isDeleteData() {
    return this.deleteData;
  }

  public void setDeleteData(boolean deleteData) {
    this.deleteData = deleteData;
    setDeleteDataIsSet(true);
  }

  public void unsetDeleteData() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DELETEDATA_ISSET_ID);
  }

  /** Returns true if field deleteData is set (has been assigned a value) and false otherwise */
  public boolean isSetDeleteData() {
    return EncodingUtils.testBit(__isset_bitfield, __DELETEDATA_ISSET_ID);
  }

  public void setDeleteDataIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DELETEDATA_ISSET_ID, value);
  }

  public boolean isIfExists() {
    return this.ifExists;
  }

  public void setIfExists(boolean ifExists) {
    this.ifExists = ifExists;
    setIfExistsIsSet(true);
  }

  public void unsetIfExists() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __IFEXISTS_ISSET_ID);
  }

  /** Returns true if field ifExists is set (has been assigned a value) and false otherwise */
  public boolean isSetIfExists() {
    return EncodingUtils.testBit(__isset_bitfield, __IFEXISTS_ISSET_ID);
  }

  public void setIfExistsIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __IFEXISTS_ISSET_ID, value);
  }

  public boolean isIgnoreProtection() {
    return this.ignoreProtection;
  }

  public void setIgnoreProtection(boolean ignoreProtection) {
    this.ignoreProtection = ignoreProtection;
    setIgnoreProtectionIsSet(true);
  }

  public void unsetIgnoreProtection() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __IGNOREPROTECTION_ISSET_ID);
  }

  /** Returns true if field ignoreProtection is set (has been assigned a value) and false otherwise */
  public boolean isSetIgnoreProtection() {
    return EncodingUtils.testBit(__isset_bitfield, __IGNOREPROTECTION_ISSET_ID);
  }

  public void setIgnoreProtectionIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __IGNOREPROTECTION_ISSET_ID, value);
  }

  public EnvironmentContext getEnvironmentContext() {
    return this.environmentContext;
  }

  public void setEnvironmentContext(EnvironmentContext environmentContext) {
    this.environmentContext = environmentContext;
  }

  public void unsetEnvironmentContext() {
    this.environmentContext = null;
  }

  /** Returns true if field environmentContext is set (has been assigned a value) and false otherwise */
  public boolean isSetEnvironmentContext() {
    return this.environmentContext != null;
  }

  public void setEnvironmentContextIsSet(boolean value) {
    if (!value) {
      this.environmentContext = null;
    }
  }

  public boolean isNeedResult() {
    return this.needResult;
  }

  public void setNeedResult(boolean needResult) {
    this.needResult = needResult;
    setNeedResultIsSet(true);
  }

  public void unsetNeedResult() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NEEDRESULT_ISSET_ID);
  }

  /** Returns true if field needResult is set (has been assigned a value) and false otherwise */
  public boolean isSetNeedResult() {
    return EncodingUtils.testBit(__isset_bitfield, __NEEDRESULT_ISSET_ID);
  }

  public void setNeedResultIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NEEDRESULT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case DB_NAME:
      if (value == null) {
        unsetDbName();
      } else {
        setDbName((String)value);
      }
      break;

    case TBL_NAME:
      if (value == null) {
        unsetTblName();
      } else {
        setTblName((String)value);
      }
      break;

    case PARTS:
      if (value == null) {
        unsetParts();
      } else {
        setParts((RequestPartsSpec)value);
      }
      break;

    case DELETE_DATA:
      if (value == null) {
        unsetDeleteData();
      } else {
        setDeleteData((Boolean)value);
      }
      break;

    case IF_EXISTS:
      if (value == null) {
        unsetIfExists();
      } else {
        setIfExists((Boolean)value);
      }
      break;

    case IGNORE_PROTECTION:
      if (value == null) {
        unsetIgnoreProtection();
      } else {
        setIgnoreProtection((Boolean)value);
      }
      break;

    case ENVIRONMENT_CONTEXT:
      if (value == null) {
        unsetEnvironmentContext();
      } else {
        setEnvironmentContext((EnvironmentContext)value);
      }
      break;

    case NEED_RESULT:
      if (value == null) {
        unsetNeedResult();
      } else {
        setNeedResult((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case DB_NAME:
      return getDbName();

    case TBL_NAME:
      return getTblName();

    case PARTS:
      return getParts();

    case DELETE_DATA:
      return Boolean.valueOf(isDeleteData());

    case IF_EXISTS:
      return Boolean.valueOf(isIfExists());

    case IGNORE_PROTECTION:
      return Boolean.valueOf(isIgnoreProtection());

    case ENVIRONMENT_CONTEXT:
      return getEnvironmentContext();

    case NEED_RESULT:
      return Boolean.valueOf(isNeedResult());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case DB_NAME:
      return isSetDbName();
    case TBL_NAME:
      return isSetTblName();
    case PARTS:
      return isSetParts();
    case DELETE_DATA:
      return isSetDeleteData();
    case IF_EXISTS:
      return isSetIfExists();
    case IGNORE_PROTECTION:
      return isSetIgnoreProtection();
    case ENVIRONMENT_CONTEXT:
      return isSetEnvironmentContext();
    case NEED_RESULT:
      return isSetNeedResult();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof DropPartitionsRequest)
      return this.equals((DropPartitionsRequest)that);
    return false;
  }

  public boolean equals(DropPartitionsRequest that) {
    if (that == null)
      return false;

    boolean this_present_dbName = true && this.isSetDbName();
    boolean that_present_dbName = true && that.isSetDbName();
    if (this_present_dbName || that_present_dbName) {
      if (!(this_present_dbName && that_present_dbName))
        return false;
      if (!this.dbName.equals(that.dbName))
        return false;
    }

    boolean this_present_tblName = true && this.isSetTblName();
    boolean that_present_tblName = true && that.isSetTblName();
    if (this_present_tblName || that_present_tblName) {
      if (!(this_present_tblName && that_present_tblName))
        return false;
      if (!this.tblName.equals(that.tblName))
        return false;
    }

    boolean this_present_parts = true && this.isSetParts();
    boolean that_present_parts = true && that.isSetParts();
    if (this_present_parts || that_present_parts) {
      if (!(this_present_parts && that_present_parts))
        return false;
      if (!this.parts.equals(that.parts))
        return false;
    }

    boolean this_present_deleteData = true && this.isSetDeleteData();
    boolean that_present_deleteData = true && that.isSetDeleteData();
    if (this_present_deleteData || that_present_deleteData) {
      if (!(this_present_deleteData && that_present_deleteData))
        return false;
      if (this.deleteData != that.deleteData)
        return false;
    }

    boolean this_present_ifExists = true && this.isSetIfExists();
    boolean that_present_ifExists = true && that.isSetIfExists();
    if (this_present_ifExists || that_present_ifExists) {
      if (!(this_present_ifExists && that_present_ifExists))
        return false;
      if (this.ifExists != that.ifExists)
        return false;
    }

    boolean this_present_ignoreProtection = true && this.isSetIgnoreProtection();
    boolean that_present_ignoreProtection = true && that.isSetIgnoreProtection();
    if (this_present_ignoreProtection || that_present_ignoreProtection) {
      if (!(this_present_ignoreProtection && that_present_ignoreProtection))
        return false;
      if (this.ignoreProtection != that.ignoreProtection)
        return false;
    }

    boolean this_present_environmentContext = true && this.isSetEnvironmentContext();
    boolean that_present_environmentContext = true && that.isSetEnvironmentContext();
    if (this_present_environmentContext || that_present_environmentContext) {
      if (!(this_present_environmentContext && that_present_environmentContext))
        return false;
      if (!this.environmentContext.equals(that.environmentContext))
        return false;
    }

    boolean this_present_needResult = true && this.isSetNeedResult();
    boolean that_present_needResult = true && that.isSetNeedResult();
    if (this_present_needResult || that_present_needResult) {
      if (!(this_present_needResult && that_present_needResult))
        return false;
      if (this.needResult != that.needResult)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_dbName = true && (isSetDbName());
    list.add(present_dbName);
    if (present_dbName)
      list.add(dbName);

    boolean present_tblName = true && (isSetTblName());
    list.add(present_tblName);
    if (present_tblName)
      list.add(tblName);

    boolean present_parts = true && (isSetParts());
    list.add(present_parts);
    if (present_parts)
      list.add(parts);

    boolean present_deleteData = true && (isSetDeleteData());
    list.add(present_deleteData);
    if (present_deleteData)
      list.add(deleteData);

    boolean present_ifExists = true && (isSetIfExists());
    list.add(present_ifExists);
    if (present_ifExists)
      list.add(ifExists);

    boolean present_ignoreProtection = true && (isSetIgnoreProtection());
    list.add(present_ignoreProtection);
    if (present_ignoreProtection)
      list.add(ignoreProtection);

    boolean present_environmentContext = true && (isSetEnvironmentContext());
    list.add(present_environmentContext);
    if (present_environmentContext)
      list.add(environmentContext);

    boolean present_needResult = true && (isSetNeedResult());
    list.add(present_needResult);
    if (present_needResult)
      list.add(needResult);

    return list.hashCode();
  }

  @Override
  public int compareTo(DropPartitionsRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetDbName()).compareTo(other.isSetDbName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDbName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dbName, other.dbName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTblName()).compareTo(other.isSetTblName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTblName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tblName, other.tblName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetParts()).compareTo(other.isSetParts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParts()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.parts, other.parts);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDeleteData()).compareTo(other.isSetDeleteData());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeleteData()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deleteData, other.deleteData);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIfExists()).compareTo(other.isSetIfExists());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIfExists()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ifExists, other.ifExists);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIgnoreProtection()).compareTo(other.isSetIgnoreProtection());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIgnoreProtection()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ignoreProtection, other.ignoreProtection);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEnvironmentContext()).compareTo(other.isSetEnvironmentContext());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEnvironmentContext()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.environmentContext, other.environmentContext);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNeedResult()).compareTo(other.isSetNeedResult());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNeedResult()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.needResult, other.needResult);
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
    StringBuilder sb = new StringBuilder("DropPartitionsRequest(");
    boolean first = true;

    sb.append("dbName:");
    if (this.dbName == null) {
      sb.append("null");
    } else {
      sb.append(this.dbName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("tblName:");
    if (this.tblName == null) {
      sb.append("null");
    } else {
      sb.append(this.tblName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("parts:");
    if (this.parts == null) {
      sb.append("null");
    } else {
      sb.append(this.parts);
    }
    first = false;
    if (isSetDeleteData()) {
      if (!first) sb.append(", ");
      sb.append("deleteData:");
      sb.append(this.deleteData);
      first = false;
    }
    if (isSetIfExists()) {
      if (!first) sb.append(", ");
      sb.append("ifExists:");
      sb.append(this.ifExists);
      first = false;
    }
    if (isSetIgnoreProtection()) {
      if (!first) sb.append(", ");
      sb.append("ignoreProtection:");
      sb.append(this.ignoreProtection);
      first = false;
    }
    if (isSetEnvironmentContext()) {
      if (!first) sb.append(", ");
      sb.append("environmentContext:");
      if (this.environmentContext == null) {
        sb.append("null");
      } else {
        sb.append(this.environmentContext);
      }
      first = false;
    }
    if (isSetNeedResult()) {
      if (!first) sb.append(", ");
      sb.append("needResult:");
      sb.append(this.needResult);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetDbName()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'dbName' is unset! Struct:" + toString());
    }

    if (!isSetTblName()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'tblName' is unset! Struct:" + toString());
    }

    if (!isSetParts()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'parts' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (environmentContext != null) {
      environmentContext.validate();
    }
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

  private static class DropPartitionsRequestStandardSchemeFactory implements SchemeFactory {
    public DropPartitionsRequestStandardScheme getScheme() {
      return new DropPartitionsRequestStandardScheme();
    }
  }

  private static class DropPartitionsRequestStandardScheme extends StandardScheme<DropPartitionsRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, DropPartitionsRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // DB_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.dbName = iprot.readString();
              struct.setDbNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TBL_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.tblName = iprot.readString();
              struct.setTblNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PARTS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.parts = new RequestPartsSpec();
              struct.parts.read(iprot);
              struct.setPartsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // DELETE_DATA
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.deleteData = iprot.readBool();
              struct.setDeleteDataIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // IF_EXISTS
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.ifExists = iprot.readBool();
              struct.setIfExistsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // IGNORE_PROTECTION
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.ignoreProtection = iprot.readBool();
              struct.setIgnoreProtectionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // ENVIRONMENT_CONTEXT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.environmentContext = new EnvironmentContext();
              struct.environmentContext.read(iprot);
              struct.setEnvironmentContextIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // NEED_RESULT
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.needResult = iprot.readBool();
              struct.setNeedResultIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, DropPartitionsRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.dbName != null) {
        oprot.writeFieldBegin(DB_NAME_FIELD_DESC);
        oprot.writeString(struct.dbName);
        oprot.writeFieldEnd();
      }
      if (struct.tblName != null) {
        oprot.writeFieldBegin(TBL_NAME_FIELD_DESC);
        oprot.writeString(struct.tblName);
        oprot.writeFieldEnd();
      }
      if (struct.parts != null) {
        oprot.writeFieldBegin(PARTS_FIELD_DESC);
        struct.parts.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.isSetDeleteData()) {
        oprot.writeFieldBegin(DELETE_DATA_FIELD_DESC);
        oprot.writeBool(struct.deleteData);
        oprot.writeFieldEnd();
      }
      if (struct.isSetIfExists()) {
        oprot.writeFieldBegin(IF_EXISTS_FIELD_DESC);
        oprot.writeBool(struct.ifExists);
        oprot.writeFieldEnd();
      }
      if (struct.isSetIgnoreProtection()) {
        oprot.writeFieldBegin(IGNORE_PROTECTION_FIELD_DESC);
        oprot.writeBool(struct.ignoreProtection);
        oprot.writeFieldEnd();
      }
      if (struct.environmentContext != null) {
        if (struct.isSetEnvironmentContext()) {
          oprot.writeFieldBegin(ENVIRONMENT_CONTEXT_FIELD_DESC);
          struct.environmentContext.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetNeedResult()) {
        oprot.writeFieldBegin(NEED_RESULT_FIELD_DESC);
        oprot.writeBool(struct.needResult);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DropPartitionsRequestTupleSchemeFactory implements SchemeFactory {
    public DropPartitionsRequestTupleScheme getScheme() {
      return new DropPartitionsRequestTupleScheme();
    }
  }

  private static class DropPartitionsRequestTupleScheme extends TupleScheme<DropPartitionsRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DropPartitionsRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.dbName);
      oprot.writeString(struct.tblName);
      struct.parts.write(oprot);
      BitSet optionals = new BitSet();
      if (struct.isSetDeleteData()) {
        optionals.set(0);
      }
      if (struct.isSetIfExists()) {
        optionals.set(1);
      }
      if (struct.isSetIgnoreProtection()) {
        optionals.set(2);
      }
      if (struct.isSetEnvironmentContext()) {
        optionals.set(3);
      }
      if (struct.isSetNeedResult()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetDeleteData()) {
        oprot.writeBool(struct.deleteData);
      }
      if (struct.isSetIfExists()) {
        oprot.writeBool(struct.ifExists);
      }
      if (struct.isSetIgnoreProtection()) {
        oprot.writeBool(struct.ignoreProtection);
      }
      if (struct.isSetEnvironmentContext()) {
        struct.environmentContext.write(oprot);
      }
      if (struct.isSetNeedResult()) {
        oprot.writeBool(struct.needResult);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DropPartitionsRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.dbName = iprot.readString();
      struct.setDbNameIsSet(true);
      struct.tblName = iprot.readString();
      struct.setTblNameIsSet(true);
      struct.parts = new RequestPartsSpec();
      struct.parts.read(iprot);
      struct.setPartsIsSet(true);
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.deleteData = iprot.readBool();
        struct.setDeleteDataIsSet(true);
      }
      if (incoming.get(1)) {
        struct.ifExists = iprot.readBool();
        struct.setIfExistsIsSet(true);
      }
      if (incoming.get(2)) {
        struct.ignoreProtection = iprot.readBool();
        struct.setIgnoreProtectionIsSet(true);
      }
      if (incoming.get(3)) {
        struct.environmentContext = new EnvironmentContext();
        struct.environmentContext.read(iprot);
        struct.setEnvironmentContextIsSet(true);
      }
      if (incoming.get(4)) {
        struct.needResult = iprot.readBool();
        struct.setNeedResultIsSet(true);
      }
    }
  }

}

