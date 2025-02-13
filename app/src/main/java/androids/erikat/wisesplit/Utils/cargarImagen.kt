package androids.erikat.wisesplit.Utils

import android.widget.ImageView
import com.bumptech.glide.Glide


fun ImageView.cargarImagen(url:String)
    {
        if(url.isNotEmpty())
        {
            //Cargamos la imagen, this.context representa el contexto de
            //la imagen, es decir, de que Activity depende la vista
            //ImageView es un contenedor de imagenes, into dice que se cargue
            //la imagen de la URL en este contenedor.
            Glide.with(this.context).load(url).into(this)

        }

    }