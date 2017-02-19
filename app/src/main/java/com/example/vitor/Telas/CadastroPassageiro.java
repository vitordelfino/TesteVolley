package com.example.vitor.Telas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vitor.Interfaces.MontarUrl;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Tools.Retorno;
import com.example.vitor.Tools.RealizaRequisicao;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.mask.Mask;
import com.example.vitor.testevolley.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CadastroPassageiro extends AppCompatActivity implements MontarUrl {

    private ProgressDialog dialog;

    private Spinner txtDeficiente;
    private EditText txtnome;
    private EditText txtcpf;
    private EditText txtrg;
    private EditText txtlogradouro;
    private EditText txtnumero;
    private EditText txtcomplemento;
    private EditText txtcep;
    private EditText txtbairro;
    private EditText txtmunicipio;
    private EditText txtnascimento;

    private String nome;
    private String cpf;
    private String rg;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String nascimento;
    private boolean isDeficiente = false;

    private Passageiro p;

    Retorno retorno = new Retorno();

    List<String> listDeficiente = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_passageiro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listDeficiente.add("Não");
        listDeficiente.add("Sim");

        ArrayAdapter userAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listDeficiente);
        txtDeficiente = (Spinner) findViewById(R.id.txtDeficiente);
        txtDeficiente.setAdapter(userAdapter);

        txtnome = (EditText) findViewById(R.id.txtNome);
        txtcpf = (EditText) findViewById(R.id.txtCPF);
        txtcpf.addTextChangedListener(Mask.insert(Mask.CPF_MASK, txtcpf));
        txtrg = (EditText) findViewById(R.id.txtRG);
        txtrg.addTextChangedListener(Mask.insert(Mask.RG_MASK, txtrg));
        txtlogradouro = (EditText) findViewById(R.id.txtLogradouro);
        txtnumero = (EditText) findViewById(R.id.txtNumero);
        txtcomplemento = (EditText) findViewById(R.id.txtComplemento);
        txtcep = (EditText) findViewById(R.id.txtCEP);
        txtcep.addTextChangedListener(Mask.insert(Mask.CEP_MASK, txtcep));
        txtbairro = (EditText) findViewById(R.id.txtBairro);
        txtmunicipio = (EditText) findViewById(R.id.txtMunicipio);
        txtnascimento = (EditText) findViewById(R.id.txtData);
        txtnascimento.addTextChangedListener(Mask.insert(Mask.DATA_MASK, txtnascimento));



    }

    public void registrar(View view){
        nome = txtnome.getText().toString();
        cpf = txtcpf.getText().toString();
        rg = txtrg.getText().toString();
        logradouro = txtlogradouro.getText().toString();
        numero = txtnumero.getText().toString();
        complemento = txtcomplemento.getText().toString();
        cep = txtcep.getText().toString();
        bairro = txtbairro.getText().toString();
        municipio = txtmunicipio.getText().toString();
        nascimento = txtnascimento.getText().toString();
        if(txtDeficiente.getSelectedItemPosition() == 1){
            isDeficiente = true;
        }
        p = new Passageiro(0,nome,cpf,rg,logradouro,numero,complemento,cep,bairro,municipio,nascimento,isDeficiente);
        dialog = ProgressDialog.show(CadastroPassageiro.this,"Processando","Realizando Cadastro....", false, true);
        dialog.setCancelable(false);
        new Thread() {
            public void run() {
                try {

                    RealizaRequisicao.getInstance().post(CadastroPassageiro.this, url(), new Login.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                result = result.getJSONObject("retorno");
                                retorno.setStatusRetorno(result.getString("status"));
                                retorno.setRetorno(Boolean.parseBoolean(result.getString("retorno")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread.sleep(1500);

                }catch (Exception e) {
                }
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (retorno.isRetorno()) {
                            AlertDialog.Builder configAlerta = new AlertDialog.Builder(CadastroPassageiro.this);

                                configAlerta.setTitle("ALERTA");
                                configAlerta.setMessage("Sua senha é o seu CPF, deseja altera-la agora?");
                                configAlerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(CadastroPassageiro.this, Simulador.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                configAlerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(CadastroPassageiro.this, "Vamos implementar esta funcao", Toast.LENGTH_LONG).show();
                                    }
                                });
                                AlertDialog alerta = configAlerta.create();
                                alerta.show();


                        } else {
                            Toast.makeText(CadastroPassageiro.this, retorno.getStatusRetorno(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });
            }
        }.start();



    }

    public void realizaCadastro(String url, final Login.VolleyCallback callback){

        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        request.add(requisicao);

    }

    @Override
    public String url() {
        return SppdTools.getInstance().getEndPoint()+"/cadastraPassageiro/"+p.geraParametros();
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}