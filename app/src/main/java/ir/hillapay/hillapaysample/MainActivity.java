package ir.hillapay.hillapaysample;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ir.hillapay.pay.sdk.DirectdebitPayModel;
import ir.hillapay.pay.sdk.HillaErrorType;
import ir.hillapay.pay.sdk.HillaPaySdk;
import ir.hillapay.pay.sdk.IpgCallbackModel;
import ir.hillapay.pay.sdk.HillaPaySdkListener;
import ir.hillapay.pay.sdk.TransactionVerifyModel;

import static ir.hillapay.pay.sdk.HillaPaySdk.verify;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtResult;
    Button btnPayment;
    EditText edtAmount;
    EditText edtPhoneNumber;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResult = findViewById(R.id.txtResult);
        btnPayment = findViewById(R.id.btnPayment);
        edtAmount = findViewById(R.id.edtAmount);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);


        btnPayment.setOnClickListener(this);

//        uid = UUID.randomUUID().toString();
        uid = "1";

        HillaPaySdk.register(this, uid);


        HillaPaySdk.openTrack(this, uid);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        HillaPaySdk.getPaymentResult(requestCode, resultCode, data, new HillaPaySdkListener() {

            @Override
            public void paymentResult(IpgCallbackModel ipgModel, boolean isSuccess) {
                if (ipgModel != null && isSuccess)
                    verify(MainActivity.this, uid, ipgModel);
                else {
                    Spanned sp = Html.fromHtml(ipgModel.toString() + "    status: " + isSuccess);
                    txtResult.setText(sp);
                }
            }

            @Override
            public void verifyResult(TransactionVerifyModel verifyModel, boolean isSuccess) {

                Spanned sp = Html.fromHtml(verifyModel.toString() + "    status: " + isSuccess);
                txtResult.setText(sp);

            }

            @Override
            public void directDebitResult(DirectdebitPayModel payModel, boolean isSuccess) {
                Spanned sp = Html.fromHtml(payModel.toString() + "    status: " + isSuccess);
                txtResult.setText(sp);
            }

            @Override
            public void failed(String message, @HillaErrorType int errorType) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPayment)
            payment();

    }


    private void payment() {
        int amount = 1000;
        if (edtAmount.getText() != null)
            try {
                amount = Integer.valueOf(edtAmount.getText().toString());
            } catch (NumberFormatException ignored) {
            }
        else
            edtAmount.setText(String.valueOf(amount));


//        String phooneNumber = "+989122763719";
        String phooneNumber = "+989126705277";
        if (edtPhoneNumber.getText() != null && !edtPhoneNumber.getText().toString().isEmpty())
            phooneNumber = edtPhoneNumber.getText().toString();
        else
            edtPhoneNumber.setText(phooneNumber);

        HillaPaySdk.payment(MainActivity.this, String.valueOf(amount), phooneNumber,
                String.valueOf(System.currentTimeMillis()),
                "test", uid, "test", "hilla sku", true);

    }

    @Override
    protected void onDestroy() {
        HillaPaySdk.closeTrack(this, uid);
        super.onDestroy();
    }
}