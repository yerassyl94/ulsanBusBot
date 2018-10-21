package com.google.zxing.client.android.result;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.zxing.Result;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;

public final class ProductResultHandler extends ResultHandler {
    private static final int[] buttons = new int[]{C0224R.string.button_product_search, C0224R.string.button_web_search, C0224R.string.button_custom_product_search};

    /* renamed from: com.google.zxing.client.android.result.ProductResultHandler$1 */
    class C02301 implements OnClickListener {
        C02301() {
        }

        public void onClick(View view) {
            ProductResultHandler.this.openGoogleShopper(((ProductParsedResult) ProductResultHandler.this.getResult()).getNormalizedProductID());
        }
    }

    public ProductResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
        showGoogleShopperButton(new C02301());
    }

    public int getButtonCount() {
        return hasCustomProductSearch() ? buttons.length : buttons.length - 1;
    }

    public int getButtonText(int index) {
        return buttons[index];
    }

    public void handleButtonPress(final int index) {
        showNotOurResults(index, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProductParsedResult productResult = (ProductParsedResult) ProductResultHandler.this.getResult();
                switch (index) {
                    case 0:
                        ProductResultHandler.this.openProductSearch(productResult.getNormalizedProductID());
                        return;
                    case 1:
                        ProductResultHandler.this.webSearch(productResult.getNormalizedProductID());
                        return;
                    case 2:
                        ProductResultHandler.this.openURL(ProductResultHandler.this.fillInCustomSearchURL(productResult.getNormalizedProductID()));
                        return;
                    default:
                        return;
                }
            }
        });
    }

    public int getDisplayTitle() {
        return C0224R.string.result_product;
    }
}
