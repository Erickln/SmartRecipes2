 package com.example.smartrecipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

 public class RecetasDisponibles extends AppCompatActivity implements View.OnClickListener {
     private RecyclerView recycler;
     DBHelper db;
     ArrayList<Receta> recetas = new ArrayList<>();
     ArrayList<Ingrediente> ingredientesEnPosesion = new ArrayList<Ingrediente>();
     ArrayList<Receta> recetasPosible = new ArrayList<>();
    int pos;
     String[] myArray;
     private static final int KEY_EDITAR_RECETA=1;
    // ImageButton Camarones, Spaguetti;
     private static final int NEW_RECIPE_CODE=0;


     FirebaseAuth mFirebaseAuth;
     FirebaseDatabase firebaseReference;
     private DatabaseReference dbRef;
     private String userID;
     private List ingredientes;
     private Map<String, String> mapRecetas;
     private FBHelper fbHelper;
     //--------------------------------------------------------------------

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_recetas_disponibles);
         recycler = findViewById(R.id.recycler2);
         //SQLiteDatabase db = getWritableDatabase();
         db = new DBHelper(this);

         /*
         Recetas[0].ingredientes[0].nombre="camarones";
         Recetas[0].ingredientes[1].nombre="crema";
         */
         Intent i = getIntent();
         ingredientesEnPosesion=(ArrayList<Ingrediente>) i.getSerializableExtra("ingredientes");
         Log.wtf("DEBUG",ingredientesEnPosesion.toString());
         ArrayList<Ingrediente> aux = new ArrayList<>();
         for (int j = 0; j < ingredientesEnPosesion.size(); j++) {
             aux.add(new Ingrediente(ingredientesEnPosesion.get(j).nombre));
         }
         ingredientesEnPosesion=aux;
         ///////////////// Carga ingredientes con Firebase////////////////
         fbHelper = new FBHelper();
         mFirebaseAuth = FirebaseAuth.getInstance();
         firebaseReference = FirebaseDatabase.getInstance();
         dbRef = firebaseReference.getReference();
         this.userID = mFirebaseAuth.getUid();




         dbRef.child("SmartRecipes").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 //For para acceder a todos los children de la base de datos.
                 for (DataSnapshot child: dataSnapshot.getChildren()) {


                     //Se crea un hashmap que se inicializa con lo que recibimos del child de firebase.
                     mapRecetas = (Map) child.getValue();
                     HashMap hashMapRecetas= (HashMap) mapRecetas;
                     ArrayList<Integer> listaLlaves = (ArrayList<Integer>) hashMapRecetas.keySet()
                             .stream()
                             .collect(Collectors.toList());
                     JSONObject eljson = new JSONObject(mapRecetas);
                     for (int j = 0; j < listaLlaves.size(); j++) {
                         try {
         //                    Log.wtf("JSON", eljson.getString(listaLlaves.get(j)+""));
                             JSONObject obj41 = eljson.getJSONObject(listaLlaves.get(j)+"");

               //              Log.wtf("DAMEELNOMBRTEALV", obj41.getString("nombre"));
                             String nombre = obj41.getString("nombre");
                             String procedimiento = obj41.getString("procedimiento");
                             JSONArray ingredientesAux = obj41.getJSONArray("ingredientes");
                             ArrayList<Ingrediente> ingredientes = new ArrayList<>();
                             for (int i = 0; i < ingredientesAux.length(); i++) {
                                 JSONObject json = new JSONObject(ingredientesAux.getString(i));
                                 ingredientes.add(new Ingrediente(json.getString("nombre")));
                             }
                             Log.wtf("RESULTADO :(",new Receta(nombre,ingredientes,procedimiento).toString()+"recetas "+(j+1));
                             recetas.add(new Receta(nombre,ingredientes,procedimiento));

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }
             //        Log.wtf("prueba80",eljson.toString());
                     /*try {
                         JSONArray jsonArray = new JSONArray(mapRecetas);

                         for (int j = 0; j < jsonArray.length(); j++) {
                             JSONObject aux = jsonArray.getJSONObject(j);
                             Log.wtf("JSON", aux.getString("nombre"));

                         }
                     } catch (JSONException e) {
                         Log.wtf("error", "No se pudo imprimir");
                         e.printStackTrace();
                     }*/




                     //Aqui es donde se guardan los ingredientes en nuestra variable local "List ingredientes".
//////////////////////ESTE METODO ES IMPORTANTE, ES LA COVERSION DE MAPA A ARRAYLIST////////////////////////////////////////////////////

                     //For para recorrer el arraylist, aqui se asignan los valores a las Views,
                     //Aunque en este caso es una simple concatenacion a un unico textView
                 }
             }


             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


         adapter();

        /* Camarones=findViewById(R.id.Camarones);
         Spaguetti=findViewById(R.id.Spaguetti);
         if (Receta.getDip("camarones a la crema",Recetas)) {
             Camarones.setVisibility(View.VISIBLE);
         }else{
             Toast.makeText(this,"No puedes camarones",Toast.LENGTH_SHORT);
         }
         if (Receta.getDip("spaguetti a la boloñesa",Recetas)) {
             Spaguetti.setVisibility(View.VISIBLE);
         }else{
             Toast.makeText(this,"No puedes spaguetti",Toast.LENGTH_SHORT);
         }  */
     }

     private void verDisponibilidad() {
         for (int i = 0; i < ingredientesEnPosesion.size(); i++) {   //Por cada ingrediente en posesión
             for (int j = 0; j < recetas.size(); j++) {              //Revisar cada receta que existe
                 for (int k = 0; k < recetas.get(j).getIngredientes().size(); k++) {  //Para ver cada ingrediente de cada receta que existe
                     String a = recetas.get(j).ingredientes.get(k).nombre;
                     Log.wtf("otraprueba",ingredientesEnPosesion.toString());
                     String b = ingredientesEnPosesion.get(i).toString();

                     if (recetas.get(j).ingredientes.get(k).nombre==ingredientesEnPosesion.get(i).nombre) { //Y ver si ese ingrediente es el que se tiene n posesión
                         recetas.get(j).ingredientes.get(k).setEnPosesion(true);
                         break;                                      //Una receta no puede tener el mismo ingrediente más de una vez
                     }
                 }
             }
         }/*
         for (int i = 0; i < Recetas.length; i++) { //Obtener disponibilidad ::Obsoleto::
             for (int j = 0; j < Recetas[i].ingredientes.size(); j++) {
                 if (Recetas[i].ingredientes.get(j).disponibilidad==false){
                     break;
                 }
                 Recetas[i].isDisponible()=true;
             }
         }
         */
         for (int i = 0; i < recetas.size(); i++) {
             if (recetas.get(i).disponibilidad() && !recetasPosible.contains(recetas.get(i))) {

                 recetasPosible.add(recetas.get(i));
             }
         }
         adapter();
     }

     public void cargar(View v){
     //    Log.wtf("TEST",recetas.toString());
       // recetas = new ArrayList<>();


         verDisponibilidad();
     }

     public void agregarReceta(View v){
         Intent i = new Intent(this, AgregarReceta.class);
         startActivityForResult(i,NEW_RECIPE_CODE);
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == NEW_RECIPE_CODE && resultCode == Activity.RESULT_OK && data != null){

             Receta resultado = (Receta) data.getSerializableExtra("nuevaReceta");
             //añadir resultado a Recetas
             recetasPosible.add(resultado);
           //  verDisponibilidad();
             adapter();
             Toast.makeText(this, "La receta " + resultado.nombre + " se ha añadido con éxito.", Toast.LENGTH_SHORT).show();
         }else if(requestCode == KEY_EDITAR_RECETA && resultCode == Activity.RESULT_OK && data != null){
             Receta resultado = (Receta) data.getSerializableExtra("recetaModificada");
             Receta resultado2 = (Receta) data.getSerializableExtra("recetaAModificar");
             recetas.set(pos,resultado);
             verDisponibilidad();
             adapter();
         }
     }

     public void adapter(){
         RecetaAdapter recetaAdapter = new RecetaAdapter(recetasPosible, this);
         LinearLayoutManager llm = new LinearLayoutManager(this);
         llm.setOrientation(LinearLayoutManager.VERTICAL);

         GridLayoutManager glm = new GridLayoutManager(this, 1);

         recycler.setLayoutManager(llm);
         recycler.setAdapter(recetaAdapter);
     }

     @Override
     public void onClick(View v) {
         pos = recycler.getChildLayoutPosition(v);
         Intent i = new Intent(this, EditarReceta.class);
         i.putExtra("recetaAModificar",recetas.get(pos));
         Toast.makeText(this, "VOY A MOSTRAR UNA ACTIVIDAD", Toast.LENGTH_SHORT).show();
         startActivityForResult(i,KEY_EDITAR_RECETA);
     }
 }