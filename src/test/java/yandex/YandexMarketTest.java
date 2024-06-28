package yandex;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class YandexMarketTest {
    private final Logger logger = LoggerFactory.getLogger(YandexMarketTest.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private List<WebElement> products = new ArrayList<>();

    private final String YANDEX_PAGE = "https://ya.ru/";
    private final By YANDEX_MARKET_LINK = By.linkText("Подборка от Яндекс Маркета");
    private final By CATALOG_BUTTON = By.xpath("//button[contains(., 'Каталог')]");
    private final By ELECTRONICS_LINK = By.xpath("//li[@data-zone-name='category-link' and .//a[contains(., 'Электроника')]]");
    private final By SMARTPHONES_LINK = By.linkText("Смартфоны");
    private final By ALL_FILTERS_LINK = By.linkText("Все фильтры");
    private final By MAX_PRICE_INPUT_TEXT = By.xpath("//input[@data-auto='range-filter-input-max']");
    private final By DIAGONAL_BUTTON = By.xpath("//button[contains(., 'Диагональ экрана')]");
    private final By ANCESTOR = By.xpath("..");
    private final By MIN_DIAGONAL_INPUT_TEXT = By.xpath(".//input[@data-auto='range-filter-input-min']");
    private final By MANUFACTURE_BUTTON = By.xpath("//button[contains(., 'Производитель')]");
    private final By LABEL = By.cssSelector("label");
    private final By SHOW_PRODUCTS_LINK = By.linkText("Показать товары");
    private final By PRODUCT_ELEMENT = By.cssSelector("[data-zone-name='productSnippet']");
    private final By PRODUCT_NAME = By.cssSelector("h3");
    private final By HIGH_RATING_BUTTON = By.xpath("//button[contains(., 'высокий рейтинг')]");
    private final By HEADER_SEARCH_INPUT_TEXT = By.id("header-search");
    private final By HEADER_SEARCH_BUTTON = By.xpath("//button[contains(., 'Найти')]");
    private final By PRODUCT_RATING = By.cssSelector("[aria-label='Рейтинг товара']");
    private final By CLOSE_YANDEX_POPUP_BUTTON = By.cssSelector("button.simple-popup__close[aria-label='Закрыть']");

    @BeforeEach
    public void startTest() {
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        actions = new Actions(driver);

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    public void endTest() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testYandexMarket() {
        openYandexPage();
        openSmartphonesYandexMarket();
        openFilter();
        selectPriceUpTo("20000");
        selectMinDiagonal("3");
        selectManufacturesCount(5);
        showProducts();
        findProductsAndTestElementsCount();
        String firstProductName = getFirstProduct();
        changeToHighRatingSorting();
        findAndSelectProduct(firstProductName);
        showProductRating();
    }

    public void openYandexPage() {
        try {
            driver.get(YANDEX_PAGE);

            // checkYandexPopUp();

            logger.info("Осуществлен переход: " + driver.getTitle());
        } catch (Exception e) {
            logger.error("Произошла ошибка перехода на страницу Яндекс", e);
        }
    }

    public void openSmartphonesYandexMarket() {
        try {
            WebElement yandexMarketLink = wait.until(ExpectedConditions.elementToBeClickable(YANDEX_MARKET_LINK));
            yandexMarketLink.click();

            switchToNewWindow();

            WebElement catalogButton = wait.until(ExpectedConditions.elementToBeClickable(CATALOG_BUTTON));
            catalogButton.click();

            WebElement electronicsLink = wait.until(ExpectedConditions.elementToBeClickable(ELECTRONICS_LINK));
            actions.moveToElement(electronicsLink).perform();

            WebElement smartphonesLink = wait.until(ExpectedConditions.elementToBeClickable(SMARTPHONES_LINK));
            smartphonesLink.click();

            logger.info("Осуществлен переход: " + driver.getTitle());
        } catch (Exception e) {
            logger.error("Произошла ошибка открытия раздела  Смартфонов в Яндекс Маркете", e);
        }
    }

    public void openFilter() {
        try {
            WebElement allFiltersLink = wait.until(ExpectedConditions.elementToBeClickable(ALL_FILTERS_LINK));
            allFiltersLink.click();

            logger.info("Осуществлен переход: " + driver.getTitle());
        } catch (Exception e) {
            logger.error("Произошла ошибка открытия полного фильтра", e);
        }
    }

    public void selectPriceUpTo(String priceUpTo) {
        try {
            WebElement maxPriceInputText = wait.until(ExpectedConditions.visibilityOfElementLocated(MAX_PRICE_INPUT_TEXT));
            maxPriceInputText.sendKeys(priceUpTo);

            logger.info("Выбрана цена до: " + priceUpTo);
        } catch (Exception e) {
            logger.error("Произошла ошибка выбора максимальной цены", e);
        }
    }

    public void selectMinDiagonal(String minDiagonal) {
        try {
            WebElement diagonalButton = wait.until(ExpectedConditions.presenceOfElementLocated(DIAGONAL_BUTTON));
            diagonalButton.click();

            WebElement diagonalFilter = diagonalButton.findElement(ANCESTOR);

            WebElement minDiagonalInputText = wait.until(ExpectedConditions.elementToBeClickable(diagonalFilter.findElement(MIN_DIAGONAL_INPUT_TEXT)));
            minDiagonalInputText.sendKeys(minDiagonal);

            logger.info("Выбрана минимальная диагональ: " + minDiagonal);
        } catch (Exception e) {
            logger.error("Произошла ошибка выбора минимальной диагонали экрана", e);
        }
    }

    public void selectManufacturesCount(int manufacturesCount) {
        try {
            WebElement manufacturerButton = wait.until(ExpectedConditions.presenceOfElementLocated(MANUFACTURE_BUTTON));

            WebElement manufactureFilter = manufacturerButton.findElement(ANCESTOR);

            List<WebElement> manufacturesLabels = manufactureFilter.findElements(LABEL);
            if (!manufacturesLabels.isEmpty()) {
                for (int i = 0; i < manufacturesCount; i++) {
                    WebElement manufactureLabel = manufacturesLabels.get(i);
                    manufactureLabel.click();
                }
            }
            logger.info("Выбрано производителей: " + manufacturesCount);
        } catch (Exception e) {
            logger.error("Произошла ошибка выбора производителей", e);
        }
    }

    public void showProducts() {
        try {
            WebElement showProductLink = wait.until(ExpectedConditions.presenceOfElementLocated(SHOW_PRODUCTS_LINK));
            showProductLink.click();

            logger.info("Применение фильтра");
        } catch (Exception e) {
            logger.error("Произошла ошибка применения фильтра", e);
        }
    }

    public void findProductsAndTestElementsCount() {
        try {
            wait.until(ExpectedConditions.numberOfElementsToBe(PRODUCT_ELEMENT, 24));

            products = driver.findElements(PRODUCT_ELEMENT);

            Assertions.assertEquals(24, products.size());

            logger.info("Найдено продуктов: " + products.size());
        } catch (Exception e) {
            logger.error("Произошла ошибка поиска продуктов", e);
        }
    }

    public String getFirstProduct() {
        try {
            WebElement firstProduct = products.get(0);

            String firstProductName = firstProduct.findElement(PRODUCT_NAME).getText();

            logger.info("Название первого продукта: " + firstProductName);

            return firstProductName;
        } catch (Exception e) {
            logger.error("Произошла ошибка сохранения первого продукта", e);
            return null;
        }
    }

    public void changeToHighRatingSorting() {
        try {
            WebElement highRatingButton = wait.until(ExpectedConditions.elementToBeClickable(HIGH_RATING_BUTTON));
            highRatingButton.click();

            logger.info("Изменен тип сортировки на высокий рейтинг");
        } catch (Exception e) {
            logger.error("Произошла ошибка изменения типа сортировки на высокий рейтинг", e);
        }
    }

    public void findAndSelectProduct(String productName) {
        try {
            WebElement headerSearchInputText = wait.until(ExpectedConditions.elementToBeClickable(HEADER_SEARCH_INPUT_TEXT));
            headerSearchInputText.sendKeys(productName);

            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(HEADER_SEARCH_BUTTON));
            searchButton.click();

            wait.until(ExpectedConditions.elementToBeClickable(By.linkText(productName))).click();

            logger.info("Осуществлен переход: " + productName);
        } catch (Exception e) {
            logger.error("Произошла ошибка перехода на страницу переданного продукта", e);
        }
    }

    public void showProductRating() {
        try {
            switchToNewWindow();

            WebElement productRating = wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCT_RATING));
            String productRatingValue = productRating.getText();

            logger.info("Рейтинг продукта " + productRatingValue);
        } catch (Exception e) {
            logger.error("Произошла ошибка перехода на страницу переданного продукта", e);
        }
    }

    public void switchToNewWindow() {
        String parentWindowHandle = driver.getWindowHandle();

        Set<String> allWindowHandles = driver.getWindowHandles();
        for (String windowHandle : allWindowHandles) {
            if (!windowHandle.equals(parentWindowHandle)) {
                driver.close();
                driver.switchTo().window(windowHandle);

                logger.info("Смена вкладки: " + driver.getTitle());

                break;
            }
        }
    }


    public void checkYandexPopUp() {
        try {
            WebElement closePopUpButton = wait.until(ExpectedConditions.elementToBeClickable(CLOSE_YANDEX_POPUP_BUTTON));
            closePopUpButton.click();
        } catch (TimeoutException | NoSuchElementException e) {
            logger.info("Отсутствует pop-up Яндекс Браузера");
        }
    }
}