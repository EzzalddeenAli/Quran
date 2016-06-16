package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model;

public class PrayObjects {
    private String prayerDay;
    private String prayerFajr;
    private String prayerDhuhr;
    private String prayerAsr;
    private String prayerMaghrib;
    private String prayerIsha;

    PrayObjects() {
        super();
    }

    public PrayObjects(String day, String fajr, String dhuhr, String asr, String maghrib, String isha) {
        this.prayerDay = day;
        this.prayerFajr = fajr;
        this.prayerDhuhr = dhuhr;
        this.prayerAsr = asr;
        this.prayerMaghrib = maghrib;
        this.prayerIsha = isha;
    }

    public String getPrayerDay() {
        return prayerDay;
    }

    public String getPrayerFajr() {
        return prayerFajr;
    }

    public String getPrayerDhuhr() {
        return prayerDhuhr;
    }

    public String getPrayerAsr() {
        return prayerAsr;
    }

    public String getPrayerMaghrib() {
        return prayerMaghrib;
    }

    public String getPrayerIsha() {
        return prayerIsha;
    }
}
