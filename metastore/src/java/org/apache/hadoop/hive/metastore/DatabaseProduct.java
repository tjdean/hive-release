package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum DatabaseProduct {
  DERBY, MYSQL, POSTGRES, ORACLE, SQLSERVER;
  static final private Log LOG = LogFactory.getLog(DatabaseProduct.class.getName());

  /**
   * Determine the database product type
   *
   * @return database product type
   */
  public static DatabaseProduct determineDatabaseProduct(String productName) {
    switch (productName) {
    case "Apache Derby":
      return DatabaseProduct.DERBY;
    case "Microsoft SQL Server":
      return DatabaseProduct.SQLSERVER;
    case "MySQL":
      return DatabaseProduct.MYSQL;
    case "Oracle":
      return DatabaseProduct.ORACLE;
    case "PostgreSQL":
      return DatabaseProduct.POSTGRES;
    default:
      String msg = "Unrecognized database product name <" + productName + ">";
      LOG.error(msg);
      throw new IllegalStateException(msg);
    }
  }
}
