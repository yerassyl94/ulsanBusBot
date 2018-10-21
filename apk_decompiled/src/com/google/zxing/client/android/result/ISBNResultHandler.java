package com.google.zxing.client.android.result;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.zxing.Result;
import com.google.zxing.client.android.C0224R;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;

public final class ISBNResultHandler extends ResultHandler {
    private static final int[] buttons = new int[]{C0224R.string.button_product_search, C0224R.string.button_book_search, C0224R.string.button_search_book_contents, C0224R.string.button_custom_product_search};

    /* renamed from: com.google.zxing.client.android.result.ISBNResultHandler$1 */
    class C02281 implements OnClickListener {
        C02281() {
        }

        public void onClick(View view) {
            ISBNResultHandler.this.openGoogleShopper(((ISBNParsedResult) ISBNResultHandler.this.getResult()).getISBN());
        }
    }

    public ISBNResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
        showGoogleShopperButton(new C02281());
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
                ISBNParsedResult isbnResult = (ISBNParsedResult) ISBNResultHandler.this.getResult();
                switch (index) {
                    case 0:
                        ISBNResultHandler.this.openProductSearch(isbnResult.getISBN());
                        return;
                    case 1:
                        ISBNResultHandler.this.openBookSearch(isbnResult.getISBN());
                        return;
                    case 2:
                        ISBNResultHandler.this.searchBookContents(isbnResult.getISBN());
                        return;
                    case 3:
                        ISBNResultHandler.this.openURL(ISBNResultHandler.this.fillInCustomSearchURL(isbnResult.getISBN()));
                        return;
                    default:
                        return;
                }
            }
        });
    }

    public int getDisplayTitle() {
        return C0224R.string.result_isbn;
    }
}
