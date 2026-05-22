# 🔍 Treasure Scanner Modu
**Fabric 1.20.4** | Spawner, Sandık ve Shulker ESP

---

## 📦 Kurulum

### Gereksinimler
- Minecraft 1.20.4
- [Fabric Loader](https://fabricmc.net/use/installer/) (0.15.7+)
- [Fabric API](https://modrinth.com/mod/fabric-api) (0.97.0+1.20.4)

### Adımlar
1. Bu klasörü bir IDE'ye aç (IntelliJ IDEA önerilir)
2. `./gradlew build` komutunu çalıştır
3. `build/libs/treasure-scanner-1.0.0.jar` dosyası oluşur
4. Bu JAR dosyasını `%appdata%\.minecraft\mods\` klasörüne koy
5. Fabric API'yi de mods klasörüne koy
6. Minecraft'ı başlat!

---

## 🎮 Kullanım

| Tuş | Fonksiyon |
|-----|-----------|
| **R** | Modu aç/kapat |

### Renkler
- 🔴 **Kırmızı kutu** → Spawner
- 🟡 **Sarı kutu** → Sandık (Chest, Barrel dahil)
- 🟣 **Mor kutu** → Shulker Box

### Tarama Alanı
- **Yatay:** Etrafındaki 8 chunk (128 blok)
- **Dikey:** Y: -55 ile Y: 200 arası
- **Güncelleme:** Her 2 saniyede bir otomatik tarar

---

## ⚠️ Not
- Sadece **yüklü chunk'ları** tarar
- Elytra ile uçarken yeni chunklar yüklendikçe otomatik güncellenir
- Performans için tarama 2 saniyede bir yapılır

---

## 🛠️ Derleme

```bash
# Windows
gradlew.bat build

# Linux/Mac  
./gradlew build
```

JAR dosyası: `build/libs/treasure-scanner-1.0.0.jar`
