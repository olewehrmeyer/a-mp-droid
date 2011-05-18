package com.mediaportal.ampdroid.data;

import com.mediaportal.ampdroid.api.IApiInterface;
import com.mediaportal.ampdroid.api.IClientControlApi;
import com.mediaportal.ampdroid.api.IMediaAccessApi;
import com.mediaportal.ampdroid.api.ITvServiceApi;

public class RemoteClient {
   private int mClientId;
   private String mClientName;
   private String mClientDescription;
   private int mRemoteAccessApiId;
   private int mTvControlApiId;
   private int mClientControlApiId;
   private String mUserName;
   private String mUserPassword;
   private boolean mUseAuth;

   private IMediaAccessApi mRemoteAccessApi;
   private ITvServiceApi mTvControlApi;
   private IClientControlApi mClientControlApi;

   public RemoteClient(int _clientId) {
      mClientId = _clientId;
   }

   public RemoteClient(int _clientId, String _clientName) {
      this(_clientId);
      mClientName = _clientName;
   }

   public RemoteClient(int _clientId, String _clientName, IMediaAccessApi _remoteAccessApi,
         ITvServiceApi _tvControlApi, IClientControlApi _clientControlApi) {
      this(_clientId, _clientName);
      mRemoteAccessApi = _remoteAccessApi;
      mTvControlApi = _tvControlApi;
      mClientControlApi = _clientControlApi;
   }

   public int getClientId() {
      return mClientId;
   }

   public void setClientId(int clientId) {
      this.mClientId = clientId;
   }

   public void setClientName(String clientName) {
      this.mClientName = clientName;
   }

   public String getClientName() {
      return mClientName;
   }

   public String getClientDescription() {
      return mClientDescription;
   }

   public void setClientDescription(String clientDescription) {
      this.mClientDescription = clientDescription;
   }

   public int getRemoteAccessApiId() {
      return mRemoteAccessApiId;
   }

   public void setRemoteAccessApiId(int mRemoteAccessApiId) {
      this.mRemoteAccessApiId = mRemoteAccessApiId;
   }

   public int getTvControlApiId() {
      return mTvControlApiId;
   }

   public void setTvControlApiId(int mTvControlApiId) {
      this.mTvControlApiId = mTvControlApiId;
   }

   public int getClientControlApiId() {
      return mClientControlApiId;
   }

   public void setClientControlApiId(int mClientControlApiId) {
      this.mClientControlApiId = mClientControlApiId;
   }

   public String getClientAddress() {
      if (!compareApiClients(0)) {// all clients have same address
         return "";
      } else {
         if (mRemoteAccessApi != null) {
            return mRemoteAccessApi.getAddress();
         }
         if (mClientControlApi != null) {
            return mClientControlApi.getAddress();
         }
         if (mTvControlApi != null) {
            return mTvControlApi.getAddress();
         }
      }
      return "No api defined";// shouldn't be possible
   }

   /**
    * Compare the api clients
    * 
    * @param _field
    *           which field to compare (0: address, 1:user, 2:pass, 3: auth)
    * @return true if similar, false otherwise
    */
   private boolean compareApiClients(int _field) {
      if (mRemoteAccessApi != null) {
         if (!compareApi(mRemoteAccessApi, mTvControlApi, _field)
               || !compareApi(mRemoteAccessApi, mClientControlApi, _field)) {
            return false;
         }
      }

      if (mTvControlApi != null) {
         if (!compareApi(mTvControlApi, mRemoteAccessApi, _field)
               || !compareApi(mTvControlApi, mClientControlApi, _field)) {
            return false;
         }
      }

      if (mClientControlApi != null) {
         if (!compareApi(mClientControlApi, mRemoteAccessApi, _field)
               || !compareApi(mClientControlApi, mTvControlApi, _field)) {
            return false;
         }
      }

      return true;
   }

   private boolean compareApi(IApiInterface _api1, IApiInterface _api2, int _field) {
      if (_api1 == null || _api2 == null)
         return true;
      switch (_field) {
      case 0:
         if (_api1.getAddress() == null || _api2.getAddress() == null) {
            return false;
         }
         return _api1.getAddress().equals(_api2.getAddress());
      case 1:
         if (_api1.getUserName() == null || _api2.getUserName() == null) {
            return false;
         }
         return _api1.getUserName().equals(_api2.getUserName());
      case 2:
         if (_api1.getUserPass() == null || _api2.getUserPass() == null) {
            return false;
         }
         return _api1.getUserPass().equals(_api2.getUserPass());
      case 3:
         return _api1.getUseAuth() == _api2.getUseAuth();
      default:
         return true;
      }

   }

   @Override
   public String toString() {
      if (mClientName != null) {
         return mClientName;
      } else {
         return "Client" + mClientId;
      }
   }

   public IMediaAccessApi getRemoteAccessApi() {
      return mRemoteAccessApi;
   }

   public void setRemoteAccessApi(IMediaAccessApi remoteAccessApi) {
      this.mRemoteAccessApi = remoteAccessApi;
   }

   public ITvServiceApi getTvControlApi() {
      return mTvControlApi;
   }

   public void setTvControlApi(ITvServiceApi tvControlApi) {
      this.mTvControlApi = tvControlApi;
   }

   public IClientControlApi getClientControlApi() {
      return mClientControlApi;
   }

   public void setClientControlApi(IClientControlApi clientControlApi) {
      this.mClientControlApi = clientControlApi;
   }

   public boolean useAuth() {
      if (!compareApiClients(3)) {// not all clients have same address
         return false;
      } else {
         return true;
      }
   }

   public String getUserPassword() {
      if (!compareApiClients(2)) {// all clients have same address
         return "Different";
      } else {
         if (mRemoteAccessApi != null) {
            return mRemoteAccessApi.getUserPass();
         }
         if (mClientControlApi != null) {
            return mClientControlApi.getUserPass();
         }
         if (mTvControlApi != null) {
            return mTvControlApi.getUserPass();
         }
         return "No api defined";// shouldn't be possible
      }
   }

   public String getUserName() {
      if (!compareApiClients(1)) {// all clients have same address
         return "Different";
      } else {
         if (mRemoteAccessApi != null) {
            return mRemoteAccessApi.getUserName();
         }
         if (mClientControlApi != null) {
            return mClientControlApi.getUserName();
         }
         if (mTvControlApi != null) {
            return mTvControlApi.getUserName();
         }
         return "No api defined";// shouldn't be possible
      }
   }

   public boolean hasDifferentSettings() {
      if (!compare(mClientControlApi.getAddress(), mRemoteAccessApi.getAddress(),
            mTvControlApi.getAddress())) {
         return true;
      }
      
      if (!compare(mClientControlApi.getUserName(), mRemoteAccessApi.getUserName(),
            mTvControlApi.getUserName())) {
         return true;
      }
      if (!compare(mClientControlApi.getUserPass(), mRemoteAccessApi.getUserPass(),
            mTvControlApi.getUserPass())) {
         return true;
      }
      if (!compare(mClientControlApi.getUseAuth(), mRemoteAccessApi.getUseAuth(),
            mTvControlApi.getUseAuth())) {
         return true;
      }
      return false;
   }

   public boolean compare(Object... compare) {
      for (Object o : compare) {
         if (!compare[0].equals(o)) {
            return false;
         }
      }
      return true;
   }

}