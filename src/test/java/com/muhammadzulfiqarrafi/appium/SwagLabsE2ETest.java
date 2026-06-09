package com.muhammadzulfiqarrafi.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class SwagLabsE2ETest {

    private AndroidDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        // Sesuaikan nama device dengan emulator/device asli Anda
        options.setDeviceName("emulator-5554");
        // Pastikan path APK sesuai dengan lokasi di komputer Anda
        options.setApp(System.getProperty("user.dir") + "/app/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk");
        options.setAppPackage("com.swaglabsmobileapp");
        options.setAppActivity("com.swaglabsmobileapp.MainActivity");
        options.setAutomationName("UiAutomator2");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    // 1. Skenario Negatif: Login dengan kredensial salah
    @Test(priority = 1)
    public void testNegativeLogin() {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("test-Username")));
        usernameInput.sendKeys("locked_out_user");

        WebElement passwordInput = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        passwordInput.sendKeys("secret_sauce");

        driver.findElement(AppiumBy.accessibilityId("test-LOGIN")).click();

        // Validasi munculnya pesan error
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("test-Error message")));
        Assert.assertTrue(errorMsg.isDisplayed(), "Error message tidak muncul untuk login yang gagal!");
    }

    // 2. Skenario Positif: Login berhasil
    @Test(priority = 2)
    public void testPositiveLogin() {
        // Clear input sebelumnya
        driver.findElement(AppiumBy.accessibilityId("test-Username")).clear();
        driver.findElement(AppiumBy.accessibilityId("test-Password")).clear();

        driver.findElement(AppiumBy.accessibilityId("test-Username")).sendKeys("standard_user");
        driver.findElement(AppiumBy.accessibilityId("test-Password")).sendKeys("secret_sauce");
        driver.findElement(AppiumBy.accessibilityId("test-LOGIN")).click();

        // Validasi berhasil masuk ke halaman Products
        WebElement productsTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView[@text='PRODUCTS']")));
        Assert.assertTrue(productsTitle.isDisplayed());
    }

    // 3. Skenario Sort: Harga Termurah ke Termahal
    @Test(priority = 3)
    public void testSortProductLowToHigh() {
        // Klik icon filter/sort
        driver.findElement(AppiumBy.accessibilityId("test-Modal Selector Button")).click();

        // Pilih opsi Low to High
        WebElement lowToHighOption = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView[@text='Price (low to high)']")));
        lowToHighOption.click();

        // (Opsional) Tambahkan validasi dengan mengambil text harga item pertama dan kedua untuk memastikan sudah terurut
    }

    // 4. Skenario E2E: Add to Cart sampai Checkout
    @Test(priority = 4)
    public void testAddToCartAndCheckout() {
        // Add product pertama ke cart
        WebElement addToCartBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("(//android.view.ViewGroup[@content-desc='test-ADD TO CART'])[1]")));
        addToCartBtn.click();

        // Buka Cart
        driver.findElement(AppiumBy.accessibilityId("test-Cart drop zone")).click();

        // Klik Checkout
        WebElement checkoutBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("test-CHECKOUT")));
        checkoutBtn.click();

        // Isi form Checkout
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("test-First Name"))).sendKeys("John");
        driver.findElement(AppiumBy.accessibilityId("test-Last Name")).sendKeys("Doe");
        driver.findElement(AppiumBy.accessibilityId("test-Zip/Postal Code")).sendKeys("12345");

        // Continue
        driver.findElement(AppiumBy.accessibilityId("test-CONTINUE")).click();

        // Finish scroll ke bawah (tergantung ukuran layar emulator, mungkin butuh scroll, asumsi tombol terlihat)
        WebElement finishBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("test-FINISH")));
        finishBtn.click();

        // Validasi sukses checkout
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView[@text='THANK YOU FOR YOU ORDER']")));
        Assert.assertTrue(successMessage.isDisplayed());
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}