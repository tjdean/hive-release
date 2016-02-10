/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.llap.security;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.llap.security.LlapTokenIdentifier;
import org.apache.hadoop.ipc.RetriableException;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.hadoop.security.token.delegation.ZKDelegationTokenSecretManager;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenManager;

public class SecretManager extends ZKDelegationTokenSecretManager<LlapTokenIdentifier> {
  private static final Log LOG = LogFactory.getLog(SecretManager.class);
  public SecretManager(Configuration conf) {
    super(conf);
  }

  @Override
  public LlapTokenIdentifier createIdentifier() {
    return new LlapTokenIdentifier();
  }

  @Override
  public LlapTokenIdentifier decodeTokenIdentifier(
      Token<LlapTokenIdentifier> token) throws IOException {
    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(token.getIdentifier()));
    LlapTokenIdentifier id = new LlapTokenIdentifier();
    id.readFields(dis);
    dis.close();
    return id;
  }

  public static SecretManager createSecretManager(
      final Configuration conf, String llapPrincipal, String llapKeytab) {
    // Create ZK connection under a separate ugi (if specified) - ZK works in mysterious ways.
    UserGroupInformation zkUgi = null;
    String principal = HiveConf.getVar(conf, ConfVars.LLAP_ZKSM_KERBEROS_PRINCIPAL, llapPrincipal);
    String keyTab = HiveConf.getVar(conf, ConfVars.LLAP_ZKSM_KERBEROS_KEYTAB_FILE, llapKeytab);
    try {
      zkUgi = LlapSecurityHelper.loginWithKerberos(principal, keyTab);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // Override the default delegation token lifetime for LLAP.
    // Also set all the necessary ZK settings to defaults and LLAP configs, if not set.
    final Configuration zkConf = new Configuration(conf);
    zkConf.setLong(DelegationTokenManager.MAX_LIFETIME,
        HiveConf.getTimeVar(conf, ConfVars.LLAP_DELEGATION_TOKEN_LIFETIME, TimeUnit.SECONDS));
    zkConf.set(SecretManager.ZK_DTSM_ZK_KERBEROS_PRINCIPAL, principal);
    zkConf.set(SecretManager.ZK_DTSM_ZK_KERBEROS_KEYTAB, keyTab);
    setZkConfIfNotSet(zkConf, SecretManager.ZK_DTSM_ZNODE_WORKING_PATH, "llapzkdtsm");
    setZkConfIfNotSet(zkConf, SecretManager.ZK_DTSM_ZK_AUTH_TYPE, "sasl");
    setZkConfIfNotSet(zkConf, SecretManager.ZK_DTSM_ZK_CONNECTION_STRING,
        HiveConf.getVar(zkConf, ConfVars.LLAP_ZKSM_ZK_CONNECTION_STRING));
    return zkUgi.doAs(new PrivilegedAction<SecretManager>() {
      @Override
      public SecretManager run() {
        SecretManager zkSecretManager = new SecretManager(zkConf);
        try {
          zkSecretManager.startThreads();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        return zkSecretManager;
      }
    });
  }

  private static void setZkConfIfNotSet(Configuration zkConf, String name, String value) {
    if (zkConf.get(name) != null) return;
    zkConf.set(name, value);
  }

  @Override
  protected DelegationKey getDelegationKey(int keyId) {
    LOG.info("getDelegationKey " + keyId);
    DelegationKey result = super.getDelegationKey(keyId);
    LOG.info("getDelegationKey " + toString(result));
    return result;
  }

  @Override
  protected DelegationTokenInformation getTokenInfo(LlapTokenIdentifier ident) {
    LOG.info("getTokenInfo " + ident);
    DelegationTokenInformation result = super.getTokenInfo(ident);
    LOG.info("getTokenInfo " + ident + ": " + toString(result));
    return result;
  }

  private static String toString(DelegationTokenInformation i) {
    // TODO: retrieve password via reflection?
    return i == null ? "null" : (i.getTrackingId()
        + " (object " + System.identityHashCode(i) + ")");
  }

  private static String toString(DelegationKey key) {
    return key == null ? "null" : (key.getKeyId() + ": " + Arrays.toString(key.getEncodedKey()));
  }

  @Override
  protected void storeDelegationKey(DelegationKey key) throws IOException {
    LOG.info("storeDelegationKey " + toString(key));
    super.storeDelegationKey(key);
  }

  @Override
  protected void updateDelegationKey(DelegationKey key) throws IOException {
    LOG.info("updateDelegationKey " + toString(key));
    super.updateDelegationKey(key);
  }

  @Override
  protected void removeStoredMasterKey(DelegationKey key) {
    LOG.info("removeStoredMasterKey " + toString(key));
    super.removeStoredMasterKey(key);
  }

  @Override
  protected void storeToken(LlapTokenIdentifier ident, DelegationTokenInformation tokenInfo)
      throws IOException {
    LOG.info("storeToken " + ident + ": " + toString(tokenInfo));
    super.storeToken(ident, tokenInfo);
  }

  @Override
  protected void updateToken(LlapTokenIdentifier ident, DelegationTokenInformation tokenInfo)
      throws IOException {
    LOG.info("updateToken " + ident + ": " + toString(tokenInfo));
    super.updateToken(ident, tokenInfo);
  }

  @Override
  protected void removeStoredToken(LlapTokenIdentifier ident) throws IOException {
    LOG.info("removeStoredToken " + ident);
    super.removeStoredToken(ident);
  }


  @Override
  public synchronized void addKey(DelegationKey key) throws IOException {
    LOG.info("addKey " + toString(key));
    super.addKey(key);
  }

  @Override
  protected void storeNewMasterKey(DelegationKey key) throws IOException {
    LOG.info("storeNewMasterKey " + toString(key));
    super.storeNewMasterKey(key);
  }

  @Override
  protected void storeNewToken(LlapTokenIdentifier ident, long renewDate)
      throws IOException {
    LOG.info("storeNewToken " + ident + ": " + renewDate);
    super.storeNewToken(ident, renewDate);
  }

  @Override
  protected void updateStoredToken(LlapTokenIdentifier ident, long renewDate)
      throws IOException {
    LOG.info("updateStoredToken " + ident + ": " + renewDate);
    super.updateStoredToken(ident, renewDate);
  }

  @Override
  public synchronized void addPersistedDelegationToken(
      LlapTokenIdentifier identifier, long renewDate) throws IOException {
    LOG.info("addPersistedDelegationToken " + identifier + ": " + renewDate);
    super.addPersistedDelegationToken(identifier, renewDate);
  }

  @Override
  protected synchronized byte[] createPassword(LlapTokenIdentifier identifier) {
    LOG.info("createPassword " + identifier);
    byte[] result = super.createPassword(identifier);
    LOG.info("createPassword " + identifier + ": " + Arrays.toString(result)); // TADA!
    return result;
  }

  @Override
  protected DelegationTokenInformation checkToken(
      LlapTokenIdentifier identifier) throws InvalidToken {
    LOG.info("checkToken " + identifier);
    try {
      DelegationTokenInformation result = super.checkToken(identifier);
      LOG.info("checkToken " + identifier + ": " + toString(result));
      return result;
    } catch (InvalidToken it) {
      LOG.info("checkToken " + identifier + ": failed", it);
      throw it;
    }
  }

  @Override
  public synchronized byte[] retrievePassword(LlapTokenIdentifier identifier)
      throws InvalidToken {
    LOG.info("retrievePassword " + identifier);
    try {
      byte[] result = super.retrievePassword(identifier);
      LOG.info("retrievePassword " + identifier + ": " + Arrays.toString(result)); // TADA!
      return result;
    } catch (InvalidToken it) {
      LOG.info("retrievePassword " + identifier + ": failed", it);
      throw it;
    }
  }

  @Override
  public synchronized void verifyToken(LlapTokenIdentifier identifier,
      byte[] password) throws InvalidToken {
    LOG.info("verifyToken " + identifier + ": " + Arrays.toString(password)); // TADA!
    try {
      super.verifyToken(identifier, password);
    } catch (InvalidToken it) {
      LOG.info("verifyToken " + identifier + ": failed", it);
      throw it;
    }
  }

  @Override
  public synchronized long renewToken(Token<LlapTokenIdentifier> token, String renewer)
      throws InvalidToken, IOException {
    LOG.info("renewToken " + token + ", " + renewer);
    long result = super.renewToken(token, renewer);
    LOG.info("renewToken " + token + ": " + result);
    return result;
  }

  @Override
  public synchronized LlapTokenIdentifier cancelToken(
      Token<LlapTokenIdentifier> token, String canceller) throws IOException {
    LOG.info("cancelToken " + token + ", " + canceller);
    LlapTokenIdentifier result = super.cancelToken(token, canceller);
    LOG.info("cancelToken " + token + ": " + result);
    return result;
  }

  @Override
  public byte[] retriableRetrievePassword(LlapTokenIdentifier identifier)
      throws InvalidToken, StandbyException, RetriableException, IOException {
    LOG.info("retriableRetrievePassword " + identifier);
    try {
      byte[] result = super.retriableRetrievePassword(identifier);
      LOG.info("retriableRetrievePassword " + identifier + ": " + Arrays.toString(result)); // TADA!
      return result;
    } catch (IOException it) {
      LOG.info("retriableRetrievePassword " + identifier + ": failed", it);
      throw it;
    }
  }
  
  
}