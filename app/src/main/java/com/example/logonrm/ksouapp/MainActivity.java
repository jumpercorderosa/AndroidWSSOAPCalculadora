package com.example.logonrm.ksouapp;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    //variaveis bases que  KSOUAP pede
    String METHOD_NAME = "Soma"; //operationName
    String SOAP_ACTION = ""; //não tem action

    String NAMESPACE = "http://danielle.com.br/"; //pega do WSDL (<xsd:import namespace)
    String SOAP_URL = "http://10.3.1.16:8080/Calculadora/Calculadora?wsdl"; //soap:address (troca local host pelo ip da maquina - ipconfig)

    SoapObject request;
    SoapPrimitive calcular;

    ProgressDialog pdialog;

    private EditText etNum1;
    private EditText etNum2;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNum1 = (EditText) findViewById(R.id.etNumero1);
        etNum2 = (EditText) findViewById(R.id.etNumero2);
        tvResult = (TextView) findViewById(R.id.tvResultado);

    }

    public void somar(View view) {
         if(!etNum1.getText().toString().isEmpty() && !etNum2.getText().toString().isEmpty()) {

             int numero1 = Integer.parseInt(etNum1.getText().toString());
             int numero2 = Integer.parseInt(etNum2.getText().toString());

             CalcularAsync calcularAsync = new CalcularAsync();
             calcularAsync.execute(numero1, numero2);
         }
    }


    private class CalcularAsync extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {

            request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("numero1", params[0]);
            request.addProperty("numero2", params[1]);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_URL);
            try {
                httpTransport.call(SOAP_ACTION, envelope);
                calcular = (SoapPrimitive) envelope.getResponse();
            } catch (Exception e) {
                e.getMessage();
            }
            return calcular.toString();
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
            pdialog.dismiss();
            Toast.makeText(getApplicationContext(), "Resultado: " + resultado, Toast.LENGTH_SHORT).show();

            tvResult.setText(resultado);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(MainActivity.this);
            pdialog.setMessage("Converting...");
            pdialog.show();
        }

    }


}
