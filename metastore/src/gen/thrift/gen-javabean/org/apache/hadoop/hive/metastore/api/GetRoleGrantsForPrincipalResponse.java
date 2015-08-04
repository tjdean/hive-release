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
public class GetRoleGrantsForPrincipalResponse implements org.apache.thrift.TBase<GetRoleGrantsForPrincipalResponse, GetRoleGrantsForPrincipalResponse._Fields>, java.io.Serializable, Cloneable, Comparable<GetRoleGrantsForPrincipalResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GetRoleGrantsForPrincipalResponse");

  private static final org.apache.thrift.protocol.TField PRINCIPAL_GRANTS_FIELD_DESC = new org.apache.thrift.protocol.TField("principalGrants", org.apache.thrift.protocol.TType.LIST, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new GetRoleGrantsForPrincipalResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new GetRoleGrantsForPrincipalResponseTupleSchemeFactory());
  }

  private List<RolePrincipalGrant> principalGrants; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PRINCIPAL_GRANTS((short)1, "principalGrants");

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
        case 1: // PRINCIPAL_GRANTS
          return PRINCIPAL_GRANTS;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PRINCIPAL_GRANTS, new org.apache.thrift.meta_data.FieldMetaData("principalGrants", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RolePrincipalGrant.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GetRoleGrantsForPrincipalResponse.class, metaDataMap);
  }

  public GetRoleGrantsForPrincipalResponse() {
  }

  public GetRoleGrantsForPrincipalResponse(
    List<RolePrincipalGrant> principalGrants)
  {
    this();
    this.principalGrants = principalGrants;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetRoleGrantsForPrincipalResponse(GetRoleGrantsForPrincipalResponse other) {
    if (other.isSetPrincipalGrants()) {
      List<RolePrincipalGrant> __this__principalGrants = new ArrayList<RolePrincipalGrant>(other.principalGrants.size());
      for (RolePrincipalGrant other_element : other.principalGrants) {
        __this__principalGrants.add(new RolePrincipalGrant(other_element));
      }
      this.principalGrants = __this__principalGrants;
    }
  }

  public GetRoleGrantsForPrincipalResponse deepCopy() {
    return new GetRoleGrantsForPrincipalResponse(this);
  }

  @Override
  public void clear() {
    this.principalGrants = null;
  }

  public int getPrincipalGrantsSize() {
    return (this.principalGrants == null) ? 0 : this.principalGrants.size();
  }

  public java.util.Iterator<RolePrincipalGrant> getPrincipalGrantsIterator() {
    return (this.principalGrants == null) ? null : this.principalGrants.iterator();
  }

  public void addToPrincipalGrants(RolePrincipalGrant elem) {
    if (this.principalGrants == null) {
      this.principalGrants = new ArrayList<RolePrincipalGrant>();
    }
    this.principalGrants.add(elem);
  }

  public List<RolePrincipalGrant> getPrincipalGrants() {
    return this.principalGrants;
  }

  public void setPrincipalGrants(List<RolePrincipalGrant> principalGrants) {
    this.principalGrants = principalGrants;
  }

  public void unsetPrincipalGrants() {
    this.principalGrants = null;
  }

  /** Returns true if field principalGrants is set (has been assigned a value) and false otherwise */
  public boolean isSetPrincipalGrants() {
    return this.principalGrants != null;
  }

  public void setPrincipalGrantsIsSet(boolean value) {
    if (!value) {
      this.principalGrants = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PRINCIPAL_GRANTS:
      if (value == null) {
        unsetPrincipalGrants();
      } else {
        setPrincipalGrants((List<RolePrincipalGrant>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PRINCIPAL_GRANTS:
      return getPrincipalGrants();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PRINCIPAL_GRANTS:
      return isSetPrincipalGrants();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GetRoleGrantsForPrincipalResponse)
      return this.equals((GetRoleGrantsForPrincipalResponse)that);
    return false;
  }

  public boolean equals(GetRoleGrantsForPrincipalResponse that) {
    if (that == null)
      return false;

    boolean this_present_principalGrants = true && this.isSetPrincipalGrants();
    boolean that_present_principalGrants = true && that.isSetPrincipalGrants();
    if (this_present_principalGrants || that_present_principalGrants) {
      if (!(this_present_principalGrants && that_present_principalGrants))
        return false;
      if (!this.principalGrants.equals(that.principalGrants))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_principalGrants = true && (isSetPrincipalGrants());
    list.add(present_principalGrants);
    if (present_principalGrants)
      list.add(principalGrants);

    return list.hashCode();
  }

  @Override
  public int compareTo(GetRoleGrantsForPrincipalResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetPrincipalGrants()).compareTo(other.isSetPrincipalGrants());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPrincipalGrants()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.principalGrants, other.principalGrants);
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
    StringBuilder sb = new StringBuilder("GetRoleGrantsForPrincipalResponse(");
    boolean first = true;

    sb.append("principalGrants:");
    if (this.principalGrants == null) {
      sb.append("null");
    } else {
      sb.append(this.principalGrants);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetPrincipalGrants()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'principalGrants' is unset! Struct:" + toString());
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class GetRoleGrantsForPrincipalResponseStandardSchemeFactory implements SchemeFactory {
    public GetRoleGrantsForPrincipalResponseStandardScheme getScheme() {
      return new GetRoleGrantsForPrincipalResponseStandardScheme();
    }
  }

  private static class GetRoleGrantsForPrincipalResponseStandardScheme extends StandardScheme<GetRoleGrantsForPrincipalResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, GetRoleGrantsForPrincipalResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PRINCIPAL_GRANTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list78 = iprot.readListBegin();
                struct.principalGrants = new ArrayList<RolePrincipalGrant>(_list78.size);
                RolePrincipalGrant _elem79;
                for (int _i80 = 0; _i80 < _list78.size; ++_i80)
                {
                  _elem79 = new RolePrincipalGrant();
                  _elem79.read(iprot);
                  struct.principalGrants.add(_elem79);
                }
                iprot.readListEnd();
              }
              struct.setPrincipalGrantsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, GetRoleGrantsForPrincipalResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.principalGrants != null) {
        oprot.writeFieldBegin(PRINCIPAL_GRANTS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.principalGrants.size()));
          for (RolePrincipalGrant _iter81 : struct.principalGrants)
          {
            _iter81.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class GetRoleGrantsForPrincipalResponseTupleSchemeFactory implements SchemeFactory {
    public GetRoleGrantsForPrincipalResponseTupleScheme getScheme() {
      return new GetRoleGrantsForPrincipalResponseTupleScheme();
    }
  }

  private static class GetRoleGrantsForPrincipalResponseTupleScheme extends TupleScheme<GetRoleGrantsForPrincipalResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, GetRoleGrantsForPrincipalResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      {
        oprot.writeI32(struct.principalGrants.size());
        for (RolePrincipalGrant _iter82 : struct.principalGrants)
        {
          _iter82.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, GetRoleGrantsForPrincipalResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TList _list83 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.principalGrants = new ArrayList<RolePrincipalGrant>(_list83.size);
        RolePrincipalGrant _elem84;
        for (int _i85 = 0; _i85 < _list83.size; ++_i85)
        {
          _elem84 = new RolePrincipalGrant();
          _elem84.read(iprot);
          struct.principalGrants.add(_elem84);
        }
      }
      struct.setPrincipalGrantsIsSet(true);
    }
  }

}

