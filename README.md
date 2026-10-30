# 👑 MohitOS — YOUR DIGITAL CONTROL CENTER
### GitHub रिपॉजिटरी, बिल्ड व डिप्लॉयमेंट गाइड

> **MohitOS: Sovereign Multi-Website, Application, and DevOps Command Center for Android**

---

## 📌 प्रोजेक्ट अवलोकन (Overview)
**MohitOS** आपका आधिकारिक, स्वायत्त और संपूर्ण डिजिटल कंट्रोल सेंटर (Digital Control Center) है। 

इसकी मुख्य विशेषता यह है कि **APK इंस्टॉल करने के बाद सभी सामान्य कॉन्फ़िगरेशन (वेबसाइट्स, APIs, डेटाबेस, Git रेपो, Vercel डिप्लॉयमेंट्स और CMS फील्ड्स) सीधे ऐप के UI से ही किए जा सकते हैं**। किसी भी सामान्य कार्य के लिए कोड में बदलाव करने की ज़रूरत नहीं है।

---

## 🚀 1. GitHub Actions द्वारा स्वचालित APK बिल्ड व रिलीज़

इस रिपॉजिटरी में `.github/workflows/android-build.yml` पहले से कॉन्फ़िगर किया गया है। जब भी आप GitHub पर नया कोड पुश करेंगे या नया वर्शन टैग बनाएंगे, यह अपने आप:
1. **यूनिट व रोबोलेक्ट्रिक टेस्ट** चलाएगा (`./gradlew testDebugUnitTest`)।
2. **Debug APK** और **Signed Release APK** तैयार करेगा।
3. **GitHub Releases** पर डाउनलोड करने योग्य APK फाइलें प्रकाशित करेगा।

### स्वचालित रिलीज़ ट्रिगर करने का तरीका:
```bash
# 1. अपने स्थानीय बदलाव कमिट करें
git commit -m "feat: release version 1.0.0"

# 2. वर्शन टैग बनाएं और पुश करें
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions तुरंत APK बिल्ड करके आपके GitHub रेपो के **Releases** सेक्शन में अपलोड कर देगा।

---

## 🔐 2. GitHub Secrets कॉन्फ़िगरेशन (रिलीज़ साइनिंग के लिए)

सुरक्षा नियमों के तहत कभी भी Keystore फाइल, पासवर्ड या टोकन GitHub में कमिट न करें। अपने GitHub रिपॉजिटरी की **Settings &rarr; Secrets and variables &rarr; Actions** में निम्नलिखित सीक्रेट्स जोड़ें:

| Secret Name | विवरण | उदाहरण / प्रारूप |
|---|---|---|
| `ANDROID_SIGNING_KEY_BASE64` | आपकी अपलोड कीस्टोर (.jks) फ़ाइल का Base64 स्ट्रिंग | `cat my-upload-key.jks \| base64 -w 0` |
| `ANDROID_KEYSTORE_PASSWORD` | कीस्टोर का मास्टर पासवर्ड | आपका सुरक्षित पासवर्ड |
| `ANDROID_KEY_PASSWORD` | की (Alias) का पासवर्ड | आपका सुरक्षित पासवर्ड |

*(नोट: यदि आप ये सीक्रेट्स कॉन्फ़िगर नहीं करते हैं, तब भी Debug APK अपने आप हमेशा बिल्ड होकर डाउनलोड के लिए तैयार रहेगा।)*

---

## 🛠️ 3. स्थानीय मशीन पर मैन्युअल बिल्ड (Local Build)

यदि आप अपने कंप्यूटर पर मैन्युअल रूप से APK बिल्ड करना चाहते हैं:

### आवश्यकताएं:
* **JDK**: OpenJDK 17 या 21
* **Android SDK**: Build Tools 36.0.0 (API 36)
* **Gradle**: 9.x या Gradle Wrapper

### कमांड्स:
```bash
# 1. टेस्ट चलाएं
./gradlew testDebugUnitTest

# 2. Debug APK बिल्ड करें (परीक्षण के लिए)
./gradlew assembleDebug
# आउटपुट: app/build/outputs/apk/debug/app-debug.apk

# 3. Release APK बिल्ड करें
./gradlew assembleRelease
# आउटपुट: app/build/outputs/apk/release/app-release.apk
```

---

## ☁️ 4. बैकएंड डिप्लॉयमेंट व डेटाबेस सेटअप (Backend & Database)

मास्टर एडमिन का बैकएंड Fastify / Node.js माइक्रोसर्विसेज और PostgreSQL पर आधारित है।

### A. डेटाबेस माइग्रेशन (Database Schema)
`docs/DATABASE_SCHEMA.sql` फ़ाइल को अपने PostgreSQL (उदा. Supabase, Neon DB, या Cloud SQL) में चलाएं:
```sql
-- प्रोजेक्ट्स और वेबसाइट्स तालिका
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(512) NOT NULL,
    project_type VARCHAR(64),
    environment VARCHAR(32),
    api_base_url VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- अपरिवर्तनीय ऑडिट लॉग्स
CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(64) PRIMARY KEY,
    actor VARCHAR(128) NOT NULL,
    action VARCHAR(128) NOT NULL,
    resource_id VARCHAR(128),
    status VARCHAR(32),
    timestamp BIGINT NOT NULL
);
```

### B. बैकएंड को Cloud Run या VPS पर चलाना
```bash
cd backend
npm install
npm run build
npm start
```
डिप्लॉय होने के बाद, अपने बैकएंड का URL ऐप के **Settings &rarr; Base URL** में दर्ज कर दें।

---

## 📱 5. इंस्टॉलेशन के बाद पहला उपयोग (First-Time Owner Setup)

1. अपने फोन पर **`app-debug.apk`** इंस्टॉल करें।
2. ऐप खोलते ही **First-Time Owner Setup Wizard** दिखाई देगा:
   * **Authorized Email**: `mohityadav802316@gmail.com`
   * **Master Bootstrap Code**: `MASTER_BOOTSTRAP_2026`
   * **Master Password**: अपना पसंदीदा नया पासवर्ड दर्ज करें।
3. **Establish Master Identity** दबाएं।
4. अब आपकी पहचान सुरक्षित हो चुकी है। अब आप ऐप से ही वेबसाइट्स जोड़ सकते हैं, APIs टेस्ट कर सकते हैं और पूरे सिस्टम को नियंत्रित कर सकते हैं!
