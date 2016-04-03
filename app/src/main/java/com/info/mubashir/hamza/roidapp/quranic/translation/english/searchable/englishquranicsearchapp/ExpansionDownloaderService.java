package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/**
 * This class demonstrates the minimal client implementation of the
 * DownloaderService from the Downloader library.
 */
public class ExpansionDownloaderService extends DownloaderService {

    // stuff for LVL -- MODIFY FOR YOUR APPLICATION!
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtbMylgP47TelkJew6lKr1mRPaZbYuAG/gJfco2hgYDPRW9whQdJ5P4d7hA8dXiRO1R8zXTWKBkdjgMnVqUs2q4S+ubBRa7+beEtWcpmGAk1q+OTDHR+t4WUfXkIkZB/0hi/7Wl+nRKTMWaXJxdgHYKJV374bAgxkh3LHtZaSYRaIo3ayuW9A8B6Vjpnp3c2CkGCSrGOtfijCblb8p/AQxX1TD8j1GXhEOr97xU0V066ti8sbmSYNFzHtzIjAqbOc23DPnTDlOCBz6xwivLXYiWoLcMEv3EUonkEf/wyDdhaHUsps3Er2XR80Cas3+xAcMMJfE3JQblTZupWYn9LOwwIDAQAB";
    // used by the preference obfuscater
    private static final byte[] SALT = new byte[] {
            1, 43, -12, -1, 54, 98, -100, -12, 43, 2, -8, -4, 9, 5, -106, -108, -33, 45, -1, 84
    };

    /**
     * This public key comes from your Android Market publisher account, and it
     * used by the LVL to validate responses from Market on your behalf.
     */
    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    /**
     * This is used by the preference obfuscater to make sure that your
     * obfuscated preferences are different than the ones used by other
     * applications.
     */
    @Override
    public byte[] getSALT() {
        return SALT;
    }

    /**
     * Fill this in with the class name for your alarm receiver. We do this
     * because receivers must be unique across all of Android (it's a good idea
     * to make sure that your receiver is in your unique package)
     */
    @Override
    public String getAlarmReceiverClassName() {
        return ExpansionAlarmReceiver.class.getName();
    }

}