package com.applicationsmobile.notesresolver;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String AUTHORITY = "com.applicationsmobile.notesprovider";
    private static final String TABLE_NAME = "notesAM";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);


    EditText idET, nomET, prenomET, testET, examenET, moyenneET;


    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver = getContentResolver();

        idET = (EditText) findViewById(R.id.idET);
        nomET = (EditText) findViewById(R.id.nomET);
        prenomET = (EditText) findViewById(R.id.prenomET);
        testET = (EditText) findViewById(R.id.testET);
        examenET = (EditText) findViewById(R.id.examenET);
        moyenneET = (EditText) findViewById(R.id.moyenneET);

        /* int id = idET.getText().toString().isEmpty() ? 0 : Integer.parseInt(idET.getText().toString());
        String nom = nomET.getText().toString().isEmpty() ? "default_nom" : nomET.getText().toString();
        String prenom = prenomET.getText().toString().isEmpty() ? "default_prenom" : prenomET.getText().toString();
        float test = testET.getText().toString().isEmpty() ? 0 : Float.parseFloat(testET.getText().toString());
        float examen = examenET.getText().toString().isEmpty() ? 0 : Float.parseFloat(examenET.getText().toString());
        float moyenne = moyenneET.getText().toString().isEmpty() ? 0 : Float.parseFloat(moyenneET.getText().toString()); */


        Button insertButton = findViewById(R.id.insererBtn);
        Button queryButton = findViewById(R.id.rechercheBtn);
        Button updateButton = findViewById(R.id.miseAjourBtn);
        Button deleteButton = findViewById(R.id.supprimerBtn);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idET.getText().toString().trim().isEmpty()) {
                    int id = Integer.parseInt(idET.getText().toString().trim());
                    String nom = nomET.getText().toString().trim();
                    String prenom = prenomET.getText().toString().trim();
                    float test = testET.getText().toString().isEmpty() ? 0 : Float.parseFloat(testET.getText().toString());
                    float examen = examenET.getText().toString().isEmpty() ? 0 : Float.parseFloat(examenET.getText().toString());
                    float moyenne = moyenneET.getText().toString().isEmpty() ? 0 : Float.parseFloat(moyenneET.getText().toString());

                    insertData(id, nom, prenom, test, examen, moyenne);
                } else {
                    Toast.makeText(MainActivity.this, "Veuillez entrer un identifiant valide!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryData();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idET.getText().toString().trim().isEmpty()) {
                    try {
                        int id = Integer.parseInt(idET.getText().toString().trim());
                        String nom = nomET.getText().toString().trim();
                        String prenom = prenomET.getText().toString().trim();
                        float test = testET.getText().toString().isEmpty() ? 0 : Float.parseFloat(testET.getText().toString());
                        float examen = examenET.getText().toString().isEmpty() ? 0 : Float.parseFloat(examenET.getText().toString());
                        float moyenne = moyenneET.getText().toString().isEmpty() ? 0 : Float.parseFloat(moyenneET.getText().toString());

                        updateData(id, nom, prenom, test, examen, moyenne);
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "ID non valide!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Veuillez entrer un identifiant valide!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idET.getText().toString().isEmpty()) {
                    int id = Integer.parseInt(idET.getText().toString());
                    deleteData();
                } else {
                    Toast.makeText(MainActivity.this, "Veuillez entrer un identifiant valide!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    private boolean doesIdExist(int id) {
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{"_id"}, "_id = ?", new String[]{String.valueOf(id)}, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    private void insertData(int id, String nom, String prenom, float test, float examen, float moyenne) {
        if (doesIdExist(id)) {
            Toast.makeText(this, "ID existe déjà!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("nom", nom);
        values.put("prenom", prenom);
        values.put("test", test);
        values.put("examen", examen);
        values.put("moyenne", moyenne);

        Uri resultUri = contentResolver.insert(CONTENT_URI, values);

        if (resultUri != null) {
            Toast.makeText(this, "Données insérées: " + resultUri.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Échec de l'insertion!", Toast.LENGTH_SHORT).show();
        }
    }


    private void queryData() {
        String idText = idET.getText().toString().trim();
        String nom = nomET.getText().toString().trim();
        String prenom = prenomET.getText().toString().trim();

        // Ensure at least one field is entered
        if (idText.isEmpty() && nom.isEmpty() && prenom.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir vos informations!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare dynamic selection
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgsList = new ArrayList<>();

        if (!idText.isEmpty()) {
            selection.append("_id = ?");
            selectionArgsList.add(idText);
        }
        if (!nom.isEmpty()) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("nom = ?");
            selectionArgsList.add(nom);
        }
        if (!prenom.isEmpty()) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("prenom = ?");
            selectionArgsList.add(prenom);
        }

        // Convert List to String array
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = contentResolver.query(
                CONTENT_URI,
                new String[]{"_id", "nom", "prenom", "test", "examen", "moyenne"},
                selection.toString(),
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
            @SuppressLint("Range") String nomResult = cursor.getString(cursor.getColumnIndex("nom"));
            @SuppressLint("Range") String prenomResult = cursor.getString(cursor.getColumnIndex("prenom"));
            @SuppressLint("Range") double test = cursor.getDouble(cursor.getColumnIndex("test"));
            @SuppressLint("Range") double examen = cursor.getDouble(cursor.getColumnIndex("examen"));
            @SuppressLint("Range") double moyenne = cursor.getDouble(cursor.getColumnIndex("moyenne"));

            // Populate the EditText fields with retrieved values
            idET.setText(String.valueOf(id));
            nomET.setText(nomResult);
            prenomET.setText(prenomResult);
            testET.setText(String.valueOf(test));
            examenET.setText(String.valueOf(examen));
            moyenneET.setText(String.valueOf(moyenne));

            Toast.makeText(this, "Données trouvées!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Pas de données correspondantes trouvées!", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }



    private void updateData(int id, String newNom, String newPrenom, float newTest, float newExamen, float newMoyenne) {
        ContentValues values = new ContentValues();

        values.put("nom", newNom);
        values.put("prenom", newPrenom);
        values.put("test", newTest);
        values.put("examen", newExamen);
        values.put("moyenne", newMoyenne);

        int rowsUpdated = getContentResolver().update(
                CONTENT_URI,  // Your Content Provider URI
                values,
                "_id = ?",
                new String[]{String.valueOf(id)}
        );

        Toast.makeText(this, "Lignes mises à jour!", Toast.LENGTH_SHORT).show();
    }

    private void deleteData() {
        if (!idET.getText().toString().trim().isEmpty()) {
            int id = Integer.parseInt(idET.getText().toString().trim());
            int rowsDeleted = contentResolver.delete(CONTENT_URI, "_id = ?", new String[]{String.valueOf(id)});
            Toast.makeText(this, "Lignes supprimées!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Échec de la suppression!", Toast.LENGTH_SHORT).show();
        }
    }

    public void RefreshToggle(View view) {

        idET.setText("");
        nomET.setText("");
        prenomET.setText("");
        testET.setText("");
        examenET.setText("");
        moyenneET.setText("");
        Toast.makeText(this, "Données effacées", Toast.LENGTH_SHORT).show();
    }
}