package mobilesv.gourmetexpress.Activitys

import android.Manifest
import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mobilesv.android.mobilesvlib.Mobile
import com.muddzdev.styleabletoastlibrary.StyleableToast
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import mobilesv.gourmetexpress.Adapters.GridAdapter
import mobilesv.gourmetexpress.Adapters.RecyclerPlatosBuscadorHolder
import mobilesv.gourmetexpress.Adapters.RecyclerRestauranteBuscadorHolder
import mobilesv.gourmetexpress.Apis.AutoResizeTextView
import mobilesv.gourmetexpress.Apis.Globales
import mobilesv.gourmetexpress.Models.EstadoNotificaciones
import mobilesv.gourmetexpress.Models.Modelo_Buscador_Platos
import mobilesv.gourmetexpress.Models.ResponseLogin
import mobilesv.gourmetexpress.R
import mobilesv.gourmetexpress.Realm.*
import mobilesv.gourmetexpress.Retrofit.ApiClient
import mobilesv.gourmetexpress.Retrofit.apiGexRetrofit
import mobilesv.gourmetexpress.fragments.FragmentFooter
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    //variables
    var contexto: Context? = null
    var funciones: Globales? = null
    var activityHeigth = 0
    var activityWidth = 0
    var m: Mobile? = null
    var editor: SharedPreferences.Editor? = null
    var editor2: SharedPreferences.Editor? = null
    //varable de conexion
    var conexion: Boolean? = false
    //variables de drawer
    internal var slider = false
    var activeCenterFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    var actualiza: Boolean = false
    private val RC_PHONE_CALL = 124
    internal val TAG = "GEX"
    private val RC_LOCATION_CONTACTS_PERM = 124
    //realm
    internal var misFuncionesRealm: FuncionesRealm? = null
    internal var realm: Realm? = null
    //valor de compra
    var CompraTotal: Float = 0f
    var json = ""
    var mensaje = ""
    var recupera = ""
    var notificacion = ""
    //notificaciones gcm
    var token = ""
    val PROPERTY_REG_ID = "registration_id"
    //variable para api de descraga
    var ApiClient: ApiClient? = null
    var apiService: apiGexRetrofit? = null
    var responsesLogin: ResponseLogin? = null
    ///deeplink
    var responsePeticion: String? = ""
    var objetoorden: EstadoNotificaciones? = null
    var id_deep: String? = ""
    //timer
    var timer: CountDownTimer? = null
    var error: Boolean = false

    //evaluar las cvompras en realm
    //objeto de compras

    internal lateinit var compras: RealmResults<Object_Compra>
    internal var platos: RealmResults<DatosCategoriaPlato>? = null
    internal var restaurantes: RealmResults<DatosRestaurante>? = null
    var platoslist: ArrayList<Modelo_Buscador_Platos>? = null
    var buscar: String? = ""
    var banderabuscar: Boolean? = false
    var poseetipos: String? = ""
    var enteropass: Int? = 0
    var oculta:Boolean = false //variable para ocultar buscador se usa en textwatcher y en click en botones del buscador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //obtener contexto
        contexto = this
        //inizializar libs
        try {
            activityHeigth = Globales.Display("h", this)
            activityWidth = Globales.Display("w", this)
            funciones = Globales(activityWidth, activityHeigth)
            ApiClient = ApiClient()
            ApiClient!!.url(funciones!!.decrypt(Globales.PREFIJO_LIC))
            if (Globales.IS_TESTING) {
                ApiClient!!.url(funciones!!.decrypt(Globales.getPrefijoURL(16)))
            } else {
                ApiClient!!.url(funciones!!.decrypt(Globales.getPrefijoURL(13)))
            }
            apiService = ApiClient!!.getClient().create(apiGexRetrofit::class.java)
            realm = funciones!!.initrealm(this, realm)
            misFuncionesRealm = FuncionesRealm()
        } catch (ed: Exception) {
            if (Globales.SHOW_CATH) {
                System.out.println("error en init libs home" + ed)
            }
        }
        VerificaSession()
        //fuentes
        buscador.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
        //analitycs
        try {
            Globales.googleAnalytics(this, "Home")
        } catch (e: Exception) {
        }
        //obtener token de gcm
        val prefs: SharedPreferences = getGCMPreferences(contexto!!)
        token = prefs.getString(PROPERTY_REG_ID, "")
        // println(token)
        //adapter
        val resultadoscat: RealmResults<CategoriasRealm> = misFuncionesRealm!!.recuperardatoscategoria(realm)

        //optine valor de intent
        val istrue: Boolean = intent.getBooleanExtra("help", false)
        //llamar al help
        llamarhelp(istrue, funciones!!.obtenerDatoUser(contexto, "email"))
        //obtiene valor de intent para mensajes
        if (intent.getStringExtra("mensaje") != null) {
            mensaje = intent.getStringExtra("mensaje")
        }
        if (intent.getStringExtra("recupera") != null) {
            recupera = intent.getStringExtra("recupera")
        }
        if (intent.getStringExtra("notificacion") != null) {
            notificacion = intent.getStringExtra("notificacion")
        }

        if (!mensaje.equals("", true)) {
            intent = Intent(this, MensajesActivity::class.java)
            intent.putExtra("mensaje", mensaje)
            startActivity(intent)
        }

        if (!notificacion.equals("", true)) {
            intent = Intent(this, MensajeNotificationsActivity::class.java)
            intent.putExtra("mensaje", notificacion)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)

        }

        //backgrounds
        funciones!!.background(contexto, R.drawable.logo_menu_android, logohome)
        //abrir drawer
        drawer.openDrawer(Gravity.END)
        drawer.closeDrawer(Gravity.END)
        drawer.setDrawerListener(object : ActionBarDrawerToggle(this,
                drawer, 0, 0) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                slider = false//is Closed
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                slider = true//is Opened
            }
        })
        //eventos on click
        btn.setOnClickListener {
            abrirDrawer()
        }
        btn_session.setOnClickListener {
            if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
                RegresaLogin()
            }else{
                funciones!!.borraDatosUser(contexto)
                funciones!!.BorrarTarjeta(contexto)
                funciones!!.BorrarNumpedido(contexto)
                funciones!!.BorrarDireccion(contexto)
                funciones!!.BorrarEstado(contexto)
                funciones!!.BorrarOrdenes(contexto)
                DeleteRegistrosCompra()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)
                finish()
            }

        }

        perfil.setOnClickListener {
            if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
                RegresaLogin()
            }else{
                irActividad(contexto!!, PerfilActivity::class.java, resources.getString(R.string.perfil))
            }
        }

        cardrelHome.setOnClickListener {
            intent = Intent(contexto, CarritoActivity::class.java)
            startActivity(intent)
        }
        logohome.setOnClickListener{
           ocultaBuscador()

        }
        //typeface a btn
        textSession.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
        //inicio de configuaracion de Navigation drawer
        initNavigationDrawer()

        navigation_view.itemIconTintList = null
        // navigation_view.menu.findItem(R.id.track).icon = resources.getDrawable(R.drawable.moto)
        //agregar fragment
        addFragment(FragmentFooter(), R.id.fragment_footer, false)
        val adapter = GridAdapter(this, R.layout.item_grid_home, resultadoscat, Globales(activityWidth, activityHeigth))
        //grid
        GridCentral.adapter = adapter

        funciones!!.actualizaNumPedido(realm!!, numeroPedidoH, textnumberPedidoH)
        //fuente a num,ero de pedido
        textnumberPedidoH.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_black.otf")
        RegistrGcm()
        //evento on click
        iconNotiHome.setOnClickListener {
            if (error) {
                intent = Intent(this, MensajeNotificationsActivity::class.java)
                intent.putExtra("mensaje", "errorpago")
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)

            } else {
                irActividad(contexto!!, TrackYourOrder::class.java, "TrackYourOrder")

            }

        }
        try {
            compras = realm!!.where(Object_Compra::class.java)
                    .greaterThanOrEqualTo("IdCompra", 1).findAll()
        } catch (e: Exception) {
            println(e.message)
        } finally {

        }
        //evaluar cantidades del carrito
        if (compras.size > 0 || funciones!!.obtenerDatoEstado(contexto, "estado") == 4) {
            actualizaNormal()
        }
        // println("id Interno" + funciones!!.obtenerDatoPedido(contexto, "id"))
        // println("id mostrar" + funciones!!.obtenerDatoPedido(contexto, "id_mostrar"))


        //quitar sombra

        if (!Globales.ID_DEEPLINK.equals("", true)) {
            id_deep = Globales.ID_DEEPLINK
            DescargarDatosdeeplink()
        }

        verifyPermission()


        if (recupera.equals("abirmenu", true)) {
            drawer.openDrawer(GravityCompat.END)
        }

        buscador()

        //// verificacion de cuando el texto sea 0 quitar buscador

        buscador.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //para buscar en platos es por rendimiento que se puso aca
                if (s!!.isNotEmpty()) {
                    contenedor_busca.visibility = View.VISIBLE
                    contentCentral.visibility = View.GONE
                } else {
                    //verifica si la variable de ocultacion es false
                    if(!oculta){
                        contenedor_busca.visibility = View.GONE
                        contentCentral.visibility = View.VISIBLE
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(buscador.windowToken, 0)
                    }

                }

                if (banderabuscar == false) {
                    if (s.length >= 3) {
                        buscar_tipo(s.toString())
                        oculta = false
                    }
                }

            }
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s!!.isNotEmpty()) {
                    //para buscar en restaurantes
                    if (banderabuscar == true) {
                        buscar_tipo(s.toString())
                        oculta = false
                    }
                }

            }
        })
        ////
    }


    fun llamarhelp(mostrar: Boolean, email: String) {
        if (mostrar) {
            val intent = Intent(this, HelpActivity::class.java)
            intent.putExtra("correo", email)
            startActivity(intent)
        }

    }


    @SuppressLint("SetTextI18n")
            //menu lateral
    fun initNavigationDrawer() {
        //agregar Fuentes
        val header = navigation_view.getHeaderView(0)

        if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
            header.nombremen.text = "Invitado"
        }else{
            if (!funciones!!.obtenerDatoUser(contexto, "nombres").equals("", true)) {
                header.nombremen.text = funciones!!.obtenerDatoUser(contexto, "nombres") + " " + funciones!!.obtenerDatoUser(contexto, "apellidos")
            } else {
                header.nombremen.text = funciones!!.obtenerDatoUser(contexto, "email")
                if (!funciones!!.obtenerDatoUser(contexto, "nombres").equals("", true)) {
                    header.nombremen.text = funciones!!.obtenerDatoUser(contexto, "nombres") + " " + funciones!!.obtenerDatoUser(contexto, "apellidos")
                } else {
                    header.nombremen.text = funciones!!.obtenerDatoUser(contexto, "email")
                }
                //fuentes
                header.bienvenidaheader.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
                header.nombremen.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")

                val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {

                    override fun onDrawerClosed(v: View?) {
                        super.onDrawerClosed(v)
                        //llamar funcion de actualizar UIX
                        ActualizaHeader(true)
                    }

                    override fun onDrawerOpened(v: View?) {
                        super.onDrawerOpened(v)
                        //llamar funcion de actualizar UIX
                        ActualizaHeader(false)
                    }
                }
                drawer.addDrawerListener(actionBarDrawerToggle)
                actionBarDrawerToggle.syncState()
            }
        }

        //asignacion de typefont
        fontNavDrawer(header.textIniciom)
        fontNavDrawer(header.textWalletm)
        fontNavDrawer(header.textDirm)
        fontNavDrawer(header.textOrdenesm)
        fontNavDrawer(header.textCallm)
        fontNavDrawer(header.textSoportem)
        fontNavDrawer(header.textChatm)
        fontNavDrawer(header.textTackm)
        fontNavDrawer(header.textHorariom)
        //eventos onclick de menu
        header.relIniciom.setOnClickListener { ClickMenu(relIniciom.id) }
        header.relWalletm.setOnClickListener { ClickMenu(relWalletm.id) }
        header.relDirm.setOnClickListener { ClickMenu(relDirm.id) }
        header.relOrdenesm.setOnClickListener { ClickMenu(relOrdenesm.id) }
        header.relCallm.setOnClickListener { ClickMenu(relCallm.id) }
        header.relSoportem.setOnClickListener { ClickMenu(relSoportem.id) }
        header.relChatm.setOnClickListener { ClickMenu(relChatm.id) }
        header.relTrackm.setOnClickListener { ClickMenu(relTrackm.id) }
        header.relHorariom.setOnClickListener { ClickMenu(relHorariom.id) }
    }


    //funcion drawer
    fun abrirDrawer() {
        if (slider) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            drawer.openDrawer(GravityCompat.END)
        }
    }

    //funcion para agregar fragments
    private fun addFragment(fragment: Fragment, Layout: Int, agregar: Boolean) {
        val bundle = Bundle()
        bundle.putString("actividad", "home")
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
                .replace(Layout, fragment)
                .commit()
        if (agregar) {
            activeCenterFragments.add(fragment)
        }
    }

    //funcion para remover todos los fragments activos
    private fun removeFragments() {
        if (activeCenterFragments.isNotEmpty()) {
            var fragmentTransaction = fragmentManager.beginTransaction()
            for (activeFragment in activeCenterFragments) {
                fragmentTransaction.remove(activeFragment)
            }
            activeCenterFragments.clear()
            fragmentTransaction.commit()
        }
    }

    //funcion de actualizar uix header
    fun ActualizaHeader(estado: Boolean) {
        if (estado) {
            perfil.visibility = View.VISIBLE
            logoHome.visibility = View.VISIBLE
            btn.gravity = Gravity.CENTER_HORIZONTAL

        } else {
            perfil.visibility = View.GONE
            logoHome.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.gravity = Gravity.LEFT
            } else {
                btn.gravity = Gravity.LEFT
            }
        }
    }

    fun checkAudioPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(contexto!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    //funcion de llamada
    fun llamada() {
        try {
            val pm = packageManager
            val hasTelephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
            if (hasTelephony) {
                if (checkAudioPermission()) {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:" + "+50322644444".trim { it <= ' ' })
                    startActivity(intent)
                } else {
                    //permisos
                    solicitaPermisos()
                }

            }
        } catch (e: Exception) {
        }
    }

    //funciones supervisoras de la memoria
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }

    //permisos
///permisos android 6.0
    private fun solicitaPermisos() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {

        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "Debes de asignar Permisos a la App",
                    RC_LOCATION_CONTACTS_PERM, *perms)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override
    fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        //llamada()
    }

    override
    fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

    }

    /*funcion de intent*/
    fun irActividad(contexto: Context, act: Class<*>, valor: String) {
        intent = Intent(contexto, act)
        intent.putExtra("mensaje", valor)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)


    }


    override fun onBackPressed() {
        //val view = this.currentFocus
        //para ocultar buscador  cuando presione back
        /* if (view != null) {
             val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
             imm.hideSoftInputFromWindow(view.windowToken, 0)
             contenedor_busca.visibility = View.GONE
             contentCentral.visibility = View.VISIBLE
             enteropass = enteropass!! + 1
             buscador.text.clear()

             if (contenedor_busca.visibility == View.GONE && enteropass == 3) {
                 finish()
             }
         }*/
        finish()
    }

    fun ocultaBuscador() {
        val view = this.currentFocus
//para ocultar buscador
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            contenedor_busca.visibility = View.GONE
            contentCentral.visibility = View.VISIBLE
            enteropass = enteropass!! + 1
            buscador.text.clear()
        }
    }


    fun DeleteRegistrosCompra() {
        try {
            val n: RealmResults<Object_Compra>
            realm!!.beginTransaction()
            n = realm!!.where(Object_Compra::class.java).findAll()
            if (n.size > 0) {
                n.deleteAllFromRealm()
            } else {
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            realm!!.commitTransaction()
        }

    }


    //funcion que devuelve token de gcm
    private fun getGCMPreferences(context: Context): SharedPreferences {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(SplashActivity::class.java.simpleName,
                Context.MODE_PRIVATE)
    }

    //funciuon que revisa si vienen datos de deep link
    fun deeplinkinprueba() {
        if (!id_deep.equals("", true)) {
            DescargarDatosdeeplink()
        }
    }

    //si hay datos descarga estado de pedido
    fun DescargarDatosdeeplink() {
        try {
            try {
                //obtencion del sufijo
                val sufijo = funciones!!.decrypt(Globales.SUFIJO_URL_PRIN)

                var callexample: Call<JsonObject>? = null
                // simplified call to request the news with already initialized service
                //System.out.println(json);
                val params = HashMap<String, String>()
                params.put("seccion", "gex_sv_estado_ordenes")
                if (!id_deep.equals("", true)) {
                    params.put("orden", id_deep!!)
                } else {
                    params.put("orden", funciones!!.obtenerDatoPedido(contexto, "id"))
                }

                callexample = apiService!!.getDatosGET(sufijo, params)
                callexample.enqueue(object : Callback<JsonObject> {

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {

                            try {
                                responsePeticion = response.body().toString()
                                objetoorden = Gson().fromJson<EstadoNotificaciones>(responsePeticion, EstadoNotificaciones::class.java)
                                if (objetoorden != null) {
                                    when (objetoorden!!.codigo_respuesta) {
                                        "100" -> ActualizaHome(objetoorden!!.orden.estado_orden)
                                        "400" -> showToastError(resources.getString(R.string.noexisteorden), R.drawable.ic_warning_24dp)
                                    }
                                }

                            } catch (e: Exception) {
                                val ns = 0
                            }

                        } else {
                            println("fail onsuscess cerrar" + response.errorBody())
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    }
                })

            } catch (e: Exception) {
            }

        } catch (e: Exception) {
        }

    }

    //actualizar uix
    fun ActualizaHome(estado_orden: String) {

        val mensaje = estado_orden.replace(" ", "")
        when (mensaje.trim().toLowerCase()) {
            "iniciada" -> {
                error = false
                cardrelHome.visibility = View.GONE
                iconNotiHome.visibility = View.VISIBLE
                funciones!!.background(contexto!!, R.drawable.amarillo, icon_check)
                intent = Intent(this, MensajeNotificationsActivity::class.java)
                intent.putExtra("mensaje", "cobrada")
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)

            }
            "cobrada" -> {
                error = false
                cardrelHome.visibility = View.GONE
                iconNotiHome.visibility = View.VISIBLE
                funciones!!.background(contexto!!, R.drawable.verde, icon_check)
                intent = Intent(this, MensajeNotificationsActivity::class.java)
                intent.putExtra("mensaje", "iniciada")
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)

            }
            "errorpago" -> {
                error = true
                cardrelHome.visibility = View.GONE
                iconNotiHome.visibility = View.VISIBLE
                funciones!!.background(contexto!!, R.drawable.rojo, icon_check)
                intent = Intent(this, MensajeNotificationsActivity::class.java)
                intent.putExtra("mensaje", "errorpago")
                intent.putExtra("jsonorden",Gson().toJson(objetoorden))
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)
                ActualizaVista()
            }

        }
    }

    //toast de error
    fun showToastError(mensaje: String, drawable: Int) {
        var st: StyleableToast = StyleableToast.Builder(this)
                .text(mensaje)
                .textColor(Color.WHITE)
                .backgroundColor(ContextCompat.getColor(contexto, R.color.rojo))
                .icon(drawable)
                .build()
        st.show()
    }

    //funcion onresume
    override fun onResume() {
        super.onResume()
        try {
            id_deep = Globales.ID_DEEPLINK
        } catch (e: Exception) {
        }
        if (Globales.descargarDeep && !id_deep.equals("", true)) {
            deeplinkinprueba()
            Globales.descargarDeep = false

        }
        VerificaSession()
    }

    //este solo se activa cuando la notificacion es estado de error y en 2 minutis regresa a vista normal
    fun ActualizaVista() {
        val INTERVAL = 1000 * 60 * 1
        timer = object : CountDownTimer(INTERVAL.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                actualizaNormal()
            }
        }.start()
    }

    //volver a vista normal de carrito
    fun actualizaNormal() {
        cardrelHome.visibility = View.VISIBLE
        iconNotiHome.visibility = View.GONE
    }

    //metodo de stop de timer
    fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    //metodo on destroy
    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    //metodo onpause
    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    //funciones onclick del menu lateral
    fun ClickMenu(id: Int) {
        when (id) {
            R.id.relIniciom -> {
                removeFragments()
                drawer!!.closeDrawers()
            }
            R.id.relWalletm -> {
                if (funciones!!.isNetworkAvailable(contexto)) {
                    if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
                        RegresaLogin()
                    }else{
                        irActividad(contexto!!, TarjetasListActivity::class.java, "Home")
                    }

                } else {
                        intent = Intent(this, ErrorActivity::class.java)
                        intent.putExtra("error", "internet")
                        startActivity(intent)
                }
                drawer!!.closeDrawers()
            }
            R.id.relDirm -> {
                if (funciones!!.isNetworkAvailable(contexto)) {
                    if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
                        RegresaLogin()
                    }else {
                        irActividad(contexto!!, SeleccionDireccion::class.java, "Home")
                    }
                } else {
                    intent = Intent(this, ErrorActivity::class.java)
                    intent.putExtra("error", "internet")
                    startActivity(intent)
                }
                drawer!!.closeDrawers()
            }
            R.id.relOrdenesm -> {
                if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
                    RegresaLogin()
                }else {
                    irActividad(contexto!!, MisOrdenesActivity::class.java, "")
                }
                drawer!!.closeDrawers()
            }
            R.id.relCallm -> {
                llamada()
                drawer!!.closeDrawers()
            }
            R.id.relSoportem -> {

                //Toast.makeText(applicationContext, "You Clicked Options B", Toast.LENGTH_SHORT).show()
                irActividad(contexto!!, SoporteActivity::class.java, "Soporte")
                //drawer!!.closeDrawers()
            }
            R.id.relChatm -> {
                if (funciones!!.isNetworkAvailable(contexto)) {
                    irActividad(contexto!!, ChatActivity::class.java, resources.getString(R.string.chatOnline))
                } else {
                    intent = Intent(this, ErrorActivity::class.java)
                    intent.putExtra("error", "internet")
                    startActivity(intent)

                }

                drawer!!.closeDrawers()
            }
            R.id.relTrackm -> {
                if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
                    RegresaLogin()
                }else {
                    irActividad(contexto!!, TrackYourOrder::class.java, "TrackYourOrder")
                }
                drawer!!.closeDrawers()
            }
            R.id.relHorariom -> {
                if (funciones!!.isNetworkAvailable(contexto)) {
                    irActividad(contexto!!, HorarioAtencion::class.java, "Horario")
                } else {
                    intent = Intent(this, ErrorActivity::class.java)
                    intent.putExtra("error", "internet")
                    startActivity(intent)
                }
                //Toast.makeText(applicationContext, "Horario", Toast.LENGTH_SHORT).show()
                drawer!!.closeDrawers()
            }
        }
    }

    //capturar textos de menu drawer
    fun fontNavDrawer(Text: AutoResizeTextView) {
        Text.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
    }

    //verificar permisos
    private fun verifyPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //solicitar ermisos
            solicitaPermisos()

        }

    }

    //dialogo
    fun llamardialog(mensaje: String) {
        val alert = AlertDialog.Builder(contexto!!, R.style.hidetitle)
        val layout: LayoutInflater = RegistrarActivity@ this.layoutInflater
        val dialogo: View = layout.inflate(R.layout.alert_dialog, null)
        alert.setView(dialogo)

        val alertDialog: AlertDialog = alert.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window: Window = alertDialog.window
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        val title: AutoResizeTextView = dialogo.findViewById(R.id.textTitle)
        val descripcion: AutoResizeTextView = dialogo.findViewById(R.id.description)
        //propiedades de textos
        title.typeface = Typeface.createFromAsset(assets, "font/helveticaneuelight.ttf")
        descripcion.typeface = Typeface.createFromAsset(assets, "font/helveticaneuelight.ttf")
        //envio de texto
        title.text = ""
        descripcion.text = mensaje

        val ok_btn: Button = dialogo.findViewById(R.id.button)
        ok_btn.setOnClickListener {
            alertDialog.hide()
        }
        alertDialog.show()
    }


    //verifica session
    fun VerificaSession() {
        val header = navigation_view.getHeaderView(0)
        if (funciones!!.obtenerDatoUser(contexto, "iduser").toInt() == 0) {
           //aca cambio de vista
            header.textWalletm.setTextColor(ContextCompat.getColor(contexto,R.color.textdireccion))
            header.textDirm.setTextColor(ContextCompat.getColor(contexto,R.color.textdireccion))
            header.textOrdenesm.setTextColor(ContextCompat.getColor(contexto,R.color.textdireccion))
            header.textTackm.setTextColor(ContextCompat.getColor(contexto,R.color.textdireccion))
        } else {
            //si tiene session activo todo
            header.textWalletm.setTextColor(ContextCompat.getColor(contexto,R.color.textomenu))
            header.textDirm.setTextColor(ContextCompat.getColor(contexto,R.color.textomenu))
            header.textOrdenesm.setTextColor(ContextCompat.getColor(contexto,R.color.textomenu))
            header.textTackm.setTextColor(ContextCompat.getColor(contexto,R.color.textomenu))
        }
    }

    //parametros de buscador
    private fun buscador() {
        try {
            //buscador listener
            buscador.setOnClickListener {
                contenedor_busca.visibility = View.VISIBLE
                contentCentral.visibility = View.GONE
                enteropass = 0
                if (banderabuscar == false) {
                    buscador.hint = getString(R.string.busqueda_platos)
                } else {
                    buscador.hint = getString(R.string.busqueda_restaurante)
                }
            }
            buscador.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    //para buscar en platos es por rendimiento que se puso aca
                    contenedor_busca.visibility = View.VISIBLE
                    contentCentral.visibility = View.GONE
                    if (banderabuscar == false) {
                        if (s!!.length >= 3) {
                            buscar_tipo(s.toString())
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s!!.isNotEmpty()) {
                        //para buscar en restaurantes
                        if (banderabuscar == true) {
                            buscar_tipo(s.toString())
                        }

                    }

                }
            })
            //botones buscador
            textoplatos.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
            textorest.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_bold.ttf")
            btn_platos.setOnClickListener {
                textoplatos.setTextColor(Color.parseColor("#F07929"))
                textorest.setTextColor(Color.parseColor("#000000"))
                textoplatos.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
                textorest.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_bold.ttf")
                lineaiz.setBackgroundColor(Color.parseColor("#F07929"))
                lineader.setBackgroundColor(Color.parseColor("#696969"))
                recicler_platos.visibility = View.VISIBLE
                recicler_restaurantes.visibility = View.GONE
                buscador.hint = getString(R.string.busqueda_platos)
                banderabuscar = false
                oculta = true
                buscador.text.clear()
            }
            btn_rest.setOnClickListener {
                textoplatos.setTextColor(Color.parseColor("#000000"))
                textorest.setTextColor(Color.parseColor("#F07929"))
                textorest.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_semibold.ttf")
                textoplatos.typeface = Typeface.createFromAsset(contexto!!.assets, "font/proxima_nova_bold.ttf")
                lineaiz.setBackgroundColor(Color.parseColor("#696969"))
                lineader.setBackgroundColor(Color.parseColor("#F07929"))
                recicler_platos.visibility = View.GONE
                recicler_restaurantes.visibility = View.VISIBLE
                banderabuscar = true
                oculta = true
                buscador.hint = getString(R.string.busqueda_restaurante)
                buscador.text.clear()


            }


        } catch (e: Exception) {
        }
    }

    //funcion maestra buscar
    private fun buscar_tipo(variable: String) {
        try {
            if (!(banderabuscar ?: true)) {
                //llenar consulta realm platos
                buscar_plato(variable)
                //llenar arraylist modificado a 1 dimension via consulta
                llenar_listado_platos()
                //llenar control de platos
                llenarplatos()
            } else {
                //llenar consulta realm restaurante
                buscar_restaurante(variable)
                //llenar control de  restaurante
                llenarrestaurantes()
            }


        } catch (e: Exception) {
        }
    }

    //llenar listado de platos
    private fun llenarplatos() {
        try {
            try {
                recicler_platos!!.adapter = null
            } catch (e: Exception) {
            }
            if (platoslist!!.size == 0) {
                buscadornoresultados()
            }
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recicler_platos.layoutManager = linearLayoutManager
            val adapter = RecyclerPlatosBuscadorHolder.RecyclerPlatosBuscadorAdapter(platoslist, this, funciones!!)
            recicler_platos!!.adapter = adapter
            adapter.notifyDataSetChanged()

        } catch (e: Exception) {
        }
    }

    //llenar listado de restaurantes
    private fun llenarrestaurantes() {
        try {
            try {
                recicler_restaurantes!!.adapter = null
            } catch (e: Exception) {
            }
            if (restaurantes!!.size == 0) {
                buscadornoresultados()
            }
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recicler_restaurantes.layoutManager = linearLayoutManager
            val adapter = RecyclerRestauranteBuscadorHolder.RecyclerRestauranteBuscadorAdapter(restaurantes, this, funciones!!)
            recicler_restaurantes!!.adapter = adapter
            adapter.notifyDataSetChanged()

        } catch (e: Exception) {
        }
    }

    //funciones para buscador de consulta a realm
    private fun buscar_restaurante(buscar: String) {
        restaurantes = realm!!.where(DatosRestaurante::class.java).contains("category_name", buscar, Case.INSENSITIVE).findAllSorted("category_name")
    }

    //funciones para buscador de consulta a realm
    private fun buscar_plato(buscar: String) {
        platos = realm!!.where(DatosCategoriaPlato::class.java).contains("detalle.plato_Descripcion", buscar, Case.INSENSITIVE).findAllSorted("nombre_Categoria")
    }


    private val COMPLEX_UNIT_DIP = 1
    fun dpToPx(context: Context, valueInDp: Float): Float {
        var metrics: DisplayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    //para llenar listado de platos , modelo aparte
    private fun llenar_listado_platos() {
        try {
            platoslist = null
            platoslist = ArrayList()
            platoslist!!.clear()
            for (i in 0 until platos!!.size) {
                for (j in 0 until platos!![i].detalle.size) {
                    val platonet = Modelo_Buscador_Platos()
                    platonet.ID_Categoria_Plato = platos!![i].iD_Categoria_Plato
                    platonet.Nombre_Categoria = platos!![i].nombre_Categoria
                    platonet.ID_Plato = platos!![i].detalle[j].iD_Plato
                    platonet.Plato_Descripcion = platos!![i].detalle[j].plato_Descripcion
                    platonet.Nombre_Plato_Orden = platos!![i].detalle[j].nombre_Plato_Orden
                    platonet.SKU_Orden = platos!![i].detalle[j].skU_Orden
                    platonet.Precio_Orden = platos!![i].detalle[j].precio_Orden
                    platonet.Personalizado_Basico = platos!![i].detalle[j].personalizado_Basico
                    try {
                        var restaurant: String? = buscar_restaurante_nombre(platos!![i].iD_categoria_restaurante)
                        platonet.posee_tipos = poseetipos
                        platonet.Nombre_Restaurante = restaurant
                    } catch (es: Exception) {
                    }
                    platonet.ID_Restaurante = platos!![i].iD_categoria_restaurante
                    platoslist!!.add(platonet)

                }
            }

        } catch (e: Exception) {
        }
    }

    //funcion para buscar nombre y posee tipos de restaurante
    private fun buscar_restaurante_nombre(nombre_rest: String): String {
        restaurantes = realm!!.where(DatosRestaurante::class.java).equalTo("virtuemart_category_id", nombre_rest).findAll()
        if (restaurantes!!.size > 0) {
            buscar = restaurantes!![0].category_name
            poseetipos = restaurantes!![0].posee_tipos
        }

        return buscar!!
    }

    //mostrar mensaje de no resultados en buscador
    private fun buscadornoresultados() {
        if (!Globales.llamarMensBus) {
            intent = Intent(this, MensajesActivity::class.java)
            intent.putExtra("mensaje", "buscador")
            startActivity(intent)
            Globales.llamarMensBus = true
        }
    }

    //registrar gcm
    fun RegistrGcm() {
        try {
            //preparar datos para peticion get
            val params = HashMap<String, String>()
            params.put("seccion", "registrar_device_token")
            params.put("device", Globales.getDeviceId(contexto))
            params.put("token", token)
            params.put("os", Globales.OS)
            // println(token)
            //descarga de datos
            val sufijo = funciones!!.decrypt(Globales.SUFIJO_URL_PRIN)
            val callexample = apiService!!.getDatosGETString(sufijo, params)
            // System.out.println(callexample.request().url())
            callexample.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    System.out.println(t.toString() + "error")
                }

                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    if (response!!.isSuccessful) {
                        try {
                            if (response.body().toString().toLowerCase().equals("true", true)) {
                                //println("se registro notificacion")
                                // println(token)
                            }
                        } catch (e: Exception) {
                            if (Globales.SHOW_CATH) {

                            }
                        }
                    }
                }

            })

        } catch (e: Exception) {
            if (Globales.SHOW_CATH) {
                System.out.println("Error en Seleccion direccion  line 136" + e)
            }

        }
    }

    //regresar a login
    fun RegresaLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in_trns, R.anim.fade_out_trns)
        finish()
    }


}



