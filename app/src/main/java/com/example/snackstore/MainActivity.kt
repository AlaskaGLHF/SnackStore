package com.example.snackstore

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snackstore.ViewModels.ClientViewModel
import com.example.snackstore.ViewModels.ClientViewModelFactory
import com.example.snackstore.entity.Client
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.snackstore.ViewModels.CartViewModel
import com.example.snackstore.ViewModels.CartViewModelFactory
import com.example.snackstore.ViewModels.EditProfileViewModel
import com.example.snackstore.ViewModels.GoodsViewModel
import com.example.snackstore.ViewModels.GoodsViewModelFactory
import com.example.snackstore.ViewModels.OrdersViewModel
import com.example.snackstore.ViewModels.OrdersViewModelFactory
import com.example.snackstore.entity.Goods
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("cart") {
            val cartViewModel: CartViewModel = viewModel()
            CartScreen(navController = navController, cartViewModel = cartViewModel)
        }

        composable("search") {
            val application = LocalContext.current.applicationContext as Application
            SearchScreen(navController = navController, application = application)
        }
        composable("tags") {
            TagsScreen(navController = navController)
        }
        composable("products_by_tag/{tag}") { backStackEntry ->
            val tag = backStackEntry.arguments?.getString("tag") ?: ""
            ProductsByTagScreen(navController, tag)

        }
        composable("personal_recommendations") {
            PersonalRecommendationsScreen(navController)
        }
        composable("splash") { SplashScreen(navController) }

        composable("productDetail/{goodId}") { backStackEntry ->
            val goodId = backStackEntry.arguments?.getString("goodId")?.toIntOrNull()
            goodId?.let {
                ProductDetailScreen(
                    goodId = it,
                    navController = navController
                )
            }
        }

    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val viewModel: ClientViewModel = viewModel(
        factory = ClientViewModelFactory(context.applicationContext as Application)
    )
    val authResult = viewModel.authResult

    val backgroundColor = Color(0xFFD6A153)

    LaunchedEffect(authResult) {
        if (authResult != null) {
            Log.d("SnackStore", "Успешная авторизация пользователя: $login")
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Логотип",
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 32.dp)
            )

            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Email", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")
                    if (login.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    } else if (!emailRegex.matches(login)) {
                        Toast.makeText(context, "Некорректный email", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.login(email = login, password = password)
                    }
                }
                ,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Войти", color = backgroundColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Регистрация")
            }
        }
    }

    LaunchedEffect(authResult) {
        if (authResult != null) {
            Log.d("SnackStore", "Успешная авторизация пользователя: $login")
            navController.navigate("splash") {
                popUpTo("login") { inclusive = true }
            }
            viewModel.resetState()
        }
    }


}

@Composable
fun RegisterScreen(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")
    val phoneRegex = Regex("^\\d{11}$")
    val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,}$")

    val context = LocalContext.current
    val viewModel: ClientViewModel = viewModel(
        factory = ClientViewModelFactory(context.applicationContext as Application)
    )
    val registrationSuccess = viewModel.registrationSuccess

    val backgroundColor = Color(0xFFD6A153)

    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                birthDate = dateFormat.format(calendar.time)
                showDatePickerDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePickerDialog = false }
        }.show()
    }

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            Log.d("SnackStore", "Успешная регистрация: $email")
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Логотип",
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 32.dp)
            )

            RegistrationTextField(value = fullName, onValueChange = { fullName = it }, label = "ФИО")
            RegistrationTextField(value = phone, onValueChange = { phone = it }, label = "Телефон")
            RegistrationTextField(value = email, onValueChange = { email = it }, label = "Почта")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { showDatePickerDialog = true }
            ) {
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {},
                    label = { Text("Дата рождения") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RegistrationTextField(
                value = password,
                onValueChange = { password = it },
                label = "Пароль",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    if (fullName.isBlank() || phone.isBlank() || email.isBlank() || birthDate.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!emailRegex.matches(email)) {
                        Toast.makeText(context, "Некорректный email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!phoneRegex.matches(phone)) {
                        Toast.makeText(context, "Телефон должен содержать 11 цифр", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!passwordRegex.matches(password)) {
                        Toast.makeText(context, "Пароль должен содержать минимум 5 символов и включать буквы и цифры", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val parts = fullName.trim().split(" ")
                    val client = Client(
                        first_name = parts.getOrNull(1),
                        second_name = parts.getOrNull(0),
                        third_name = parts.getOrNull(2),
                        birth_day = birthDate,
                        phone_number = phone,
                        email = email,
                        password = password
                    )
                    Log.d("SnackStore", "Попытка регистрации: $email")
                    viewModel.register(client)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Зарегистрироваться", color = backgroundColor)
            }
        }
    }
}

@Composable
fun RegistrationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    textColor: Color = Color.White
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = textColor) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            unfocusedIndicatorColor = textColor,
            focusedIndicatorColor = textColor,
            cursorColor = textColor
        )
    )
}

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6A153)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }

    LaunchedEffect(Unit) {
        delay(5000)
        navController.navigate("main") {
            popUpTo("login") { inclusive = true }
            popUpTo("splash") { inclusive = true }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application)),
    cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val goodsList by goodsViewModel.goodsList.collectAsState()
    val favorites by goodsViewModel.favoriteGoodsIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(navController)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                BannerSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(8.dp)
                )
            }

            item {
                Button(
                    onClick = { navController.navigate("personal_recommendations") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6A153))
                ) {
                    Text("Персональные рекомендации", color = Color.White)
                }
            }

            item {
                ProductGrid(
                    navController = navController,
                    goodsList = goodsList,
                    favorites = favorites,
                    onToggleFavorite = { goodsViewModel.toggleFavorite(it) },
                    onAddToCart = { good -> cartViewModel.addToCart(good) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalRecommendationsScreen(
    navController: NavHostController,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application)),
    cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val allGoods by goodsViewModel.goodsList.collectAsState()
    val recommendations = remember(allGoods) { allGoods.shuffled().take(10) }
    val favorites by goodsViewModel.favoriteGoodsIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Персональные рекомендации") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFD6A153),
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )

        if (recommendations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет доступных товаров")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                item {
                    ProductGrid(
                        navController = navController,
                        goodsList = recommendations,
                        favorites = favorites,
                        onToggleFavorite = { goodsViewModel.toggleFavorite(it) },
                        onAddToCart = { cartViewModel.addToCart(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BannerSlider(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val images = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3
    )

    val urls = listOf(
        "https://dobrycola-promo.ru/",
        "https://dobry.ru/tastes/",
        "https://dobrycola-promo.ru/"
    )

    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            currentPage = (currentPage + 1) % images.size
        }
    }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = images[currentPage]),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val url = urls[currentPage]
                    if (url.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                },
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
                .align(Alignment.CenterStart)
                .clickable {
                    currentPage = if (currentPage - 1 < 0) images.lastIndex else currentPage - 1
                }
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
                .align(Alignment.CenterEnd)
                .clickable {
                    currentPage = (currentPage + 1) % images.size
                }
        )
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD6A153))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Меню",
            tint = Color.White,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clickable {
                    navController.navigate("tags")
                }
        )
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.clickable {
                navController.navigate("search")
            }
        )
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.clickable {
                navController.navigate("cart")
            }
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.clickable {
                navController.navigate("profile")
            }
        )
    }
}

@Composable
fun TagsScreen(
    navController: NavHostController,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application)),
) {
    val tags by goodsViewModel.allTags.collectAsState()
    val accentColor = Color(0xFFD6A153)

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = accentColor)
            }
            Text(
                text = "Категории",
                style = MaterialTheme.typography.headlineSmall,
                color = accentColor
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tags) { tag ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("products_by_tag/$tag")
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF9F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.bodyLarge.copy(color = accentColor),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductsByTagScreen(
    navController: NavHostController,
    tag: String,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application)),
    cartViewModel: CartViewModel = viewModel()
) {
    LaunchedEffect(tag) {
        goodsViewModel.selectTag(tag)
    }

    val accentColor = Color(0xFFD6A153)
    val goods by goodsViewModel.goodsByTag.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = accentColor)
            }
            Text(
                text = tag,
                style = MaterialTheme.typography.headlineMedium,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (goods.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Товаров в этой категории пока нет", color = accentColor)
            }
        } else {
            val favoriteIds by goodsViewModel.favoriteGoodsIds.collectAsState()

            GoodsGrid(
                goods = goods,
                favoriteIds = favoriteIds,
                modifier = Modifier.fillMaxSize(),
                onItemClick = { good ->
                    navController.navigate("productDetail/${good.id}")
                },
                onAddToCart = { good ->
                    cartViewModel.addToCart(good)
                    Toast.makeText(context, "Товар '${good.name}' добавлен в корзину", Toast.LENGTH_SHORT).show()
                },
                onToggleFavorite = { good -> goodsViewModel.toggleFavorite(good) }
            )
        }
    }
}

@Composable
fun GoodsGrid(
    goods: List<Goods>,
    favoriteIds: Set<Int> = emptySet(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onItemClick: ((Goods) -> Unit)? = null,
    onAddToCart: (Goods) -> Unit = {},
    onToggleFavorite: (Goods) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(goods) { good ->
            ProductCard(
                good = good,
                isLiked = favoriteIds.contains(good.id),
                onAddToCart = onAddToCart,
                onToggleFavorite = onToggleFavorite,
                onClick = { onItemClick?.invoke(good) }
            )
        }
    }
}

@Composable
fun SearchScreen(
    navController: NavHostController,
    application: Application
) {
    val viewModel: GoodsViewModel = viewModel(
        factory = GoodsViewModelFactory(application)
    )

    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()

    val accentColor = Color(0xFFD6A153)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = accentColor
                )
            }
            Text(
                text = "Поиск товаров",
                style = MaterialTheme.typography.titleMedium.copy(color = accentColor),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchGoods(it)
            },
            label = { Text("Введите название товара", color = accentColor) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFDF9F2),
                unfocusedContainerColor = Color(0xFFFDF9F2),
                focusedIndicatorColor = accentColor,
                unfocusedIndicatorColor = accentColor.copy(alpha = 0.5f),
                cursorColor = accentColor,
                focusedLabelColor = accentColor,
                unfocusedLabelColor = accentColor.copy(alpha = 0.7f),
                focusedTextColor = accentColor,
                unfocusedTextColor = accentColor.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(searchResults) { good ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF9F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            navController.navigate("productDetail/${good.id}")
                        }
                ) {
                    Text(
                        text = good.name ?: "",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(color = accentColor)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    navController: NavController,
    modifier: Modifier = Modifier,
    goodsList: List<Goods>,
    favorites: Set<Int>,
    onToggleFavorite: (Goods) -> Unit,
    onAddToCart: (Goods) -> Unit,
) {
    Column(modifier = modifier.padding(8.dp)) {
        goodsList.chunked(2).forEach { rowGoods ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (good in rowGoods) {
                    ProductCard(
                        good = good,
                        isLiked = favorites.contains(good.id),
                        modifier = Modifier.weight(1f),
                        onToggleFavorite = onToggleFavorite,
                        onAddToCart = onAddToCart,
                        onClick = { navController.navigate("productDetail/${good.id}") }
                    )
                }
                if (rowGoods.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    good: Goods,
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    onAddToCart: (Goods) -> Unit,
    onToggleFavorite: (Goods) -> Unit,
    onClick: (Goods) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick(good) }
            .width(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Image(
                painter = painterResource(id = getImageResByName(good.image_path)),
                contentDescription = good.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isLiked) "Liked" else "Not liked",
                tint = if (isLiked) Color.Red else Color.Black,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clickable {
                        onToggleFavorite(good)
                    }
            )
        }

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(good.name ?: "Без имени", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${good.price ?: "0"} руб" + if ((good.discount ?: 0) > 0) " (-${good.discount}%)" else "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onAddToCart(good)
                    Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD6A153)
                )
            ) {
                Text("В корзину")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProductDetailScreen(
    goodId: Int,
    navController: NavHostController,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application)),
    cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current
    val good by produceState<Goods?>(initialValue = null) {
        value = goodsViewModel.getGoodById(goodId)
    }

    val favoriteGoodsIds by goodsViewModel.favoriteGoodsIds.collectAsState()
    val isLiked = good?.id?.let { favoriteGoodsIds.contains(it) } == true
    val accentColor = Color(0xFFD6A153)

    good?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(id = getImageResByName(it.image_path)),
                    contentDescription = it.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = accentColor
                    )
                }

                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Удалить из избранного" else "Добавить в избранное",
                    tint = if (isLiked) Color.Red else Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                        .size(32.dp)
                        .clickable {
                            goodsViewModel.toggleFavorite(it)
                        }
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = it.name ?: "Без имени",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${it.price ?: 0} руб" + if ((it.discount ?: 0) > 0) " (-${it.discount}%)" else "",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = it.description ?: "Описание отсутствует.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        cartViewModel.addToCart(it)
                        Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Добавить в корзину", color = Color.White)
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFFD6A153))
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun getImageResByName(imageName: String?): Int {
    val context = LocalContext.current
    if (imageName.isNullOrEmpty()) return R.drawable.fool
    val resourceName = imageName.substringBeforeLast('.')
    return context.resources.getIdentifier(resourceName, "drawable", context.packageName).takeIf { it != 0 } ?: R.drawable.fool
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    clientViewModel: ClientViewModel = viewModel(factory = ClientViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current

    val sharedPrefs = context.getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
    val clientId = sharedPrefs.getLong("client_id", -1L)

    val clientFlow = remember(clientId) {
        if (clientId != -1L) clientViewModel.getClientById(clientId)
        else null
    }

    val client by clientFlow?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("Избранное", "Заказы")

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFFD6A153))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(90.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = client?.first_name ?: "Имя",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = client?.birth_day ?: "00.00.0000",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = client?.phone_number ?: "+70000000000",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = client?.email ?: "email@email.ru",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = {
                        navController.navigate("edit_profile")
                    },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFFD6A153)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> FavoriteList(navController = navController)
            1 -> OrdersList()
        }
    }
}

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    clientViewModel: ClientViewModel = viewModel(
        factory = ClientViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val context = LocalContext.current.applicationContext as Application

    val editProfileViewModel: EditProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EditProfileViewModel(context) as T
            }
        }
    )

    val client by editProfileViewModel.client.collectAsState()

    LaunchedEffect(Unit) {
        delay(100)
        Log.d("EditProfileScreen", "Пробуем прочитать client после задержки: $client")
    }

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(client) {

        client?.let {
            fullName = it.first_name ?: ""
            phone = it.phone_number ?: ""
            email = it.email ?: ""
            birthDate = it.birth_day ?: ""
            password = it.password ?: ""
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                birthDate = dateFormat.format(calendar.time)
                showDatePickerDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePickerDialog = false }
        }.show()
    }

    val backgroundColor = Color.White
    val textColor = Color(0xFFD6A153)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Изменение профиля",
                color = textColor,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Логотип",
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 32.dp)
            )

            RegistrationTextField(value = fullName, onValueChange = { fullName = it }, label = "ФИО", textColor = textColor)
            RegistrationTextField(value = phone, onValueChange = { phone = it }, label = "Телефон", textColor = textColor)
            RegistrationTextField(value = email, onValueChange = { email = it }, label = "Почта", textColor = textColor)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { showDatePickerDialog = true }
            ) {
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {},
                    label = { Text("Дата рождения") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RegistrationTextField(
                value = password,
                onValueChange = { password = it },
                label = "Пароль",
                isPassword = true,
                textColor = textColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    Log.d("EditProfileScreen", "Кнопка нажата, client: $client")

                    val currentClient = client ?: run {
                        Log.w("EditProfileScreen", "client is null!")
                        return@Button
                    }

                    val updatedClient = currentClient.copy(
                        first_name = fullName,
                        phone_number = phone,
                        email = email,
                        birth_day = birthDate,
                        password = password
                    )

                    Log.d("EditProfileScreen", "Обновляем профиль: $updatedClient")

                    editProfileViewModel.updateClient(updatedClient)

                    clientViewModel.loadClient(updatedClient.id.toLong())
                    Log.d("EditProfileScreen", "loadClient вызван с id: ${updatedClient.id}")

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = textColor)
            ) {
                Text("Сохранить", color = Color.White)
            }
        }
    }
}

@Composable
fun FavoriteList(
    navController: NavController,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application)),
    cartViewModel: CartViewModel = viewModel()
) {
    val favorites by goodsViewModel.favoriteGoodsList.collectAsState()
    val favoriteIds by goodsViewModel.favoriteGoodsIds.collectAsState()

    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(favorites) { good ->
            ProductCard(
                good = good,
                isLiked = favoriteIds.contains(good.id),
                onAddToCart = { cartViewModel.addToCart(it) },
                onToggleFavorite = { goodsViewModel.toggleFavorite(it) },
                onClick = { selectedGood ->
                    navController.navigate("productDetail/${selectedGood.id}")
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersList() {
    val context = LocalContext.current
    val viewModel: OrdersViewModel = viewModel(
        factory = OrdersViewModelFactory(context.applicationContext as Application)
    )

    val orders by viewModel.ordersWithGoods.collectAsState()

    val sortedOrders = remember(orders) {
        orders.sortedByDescending { it.orderId }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sortedOrders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD6A153)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Дата заказа: ${order.date ?: "Неизвестно"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Сумма: ${order.totalPrice} ₽",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(order.goods) { good ->
                            Image(
                                painter = painterResource(id = getImageResByName(good.imagePath)),
                                contentDescription = good.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    ordersViewModel: OrdersViewModel = viewModel(
        factory = OrdersViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val context = LocalContext.current

    var address by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cartViewModel.orderCreated.collect { orderId ->
            Toast.makeText(context, "Заказ #$orderId оформлен успешно", Toast.LENGTH_SHORT).show()
            ordersViewModel.reloadOrders()
            address = ""
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Корзина", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                }
            },
            actions = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Корзина",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD6A153))
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Введите адрес доставки") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            val imageRes = getImageResByName(item.good.image_path)

                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = item.good.name ?: "Неизвестный товар",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Кол-во: ${item.cartItem.quantity}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Цена: ${item.good.price} ₽",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                cartViewModel.removeFromCart(item.good.id)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить из корзины",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (address.isBlank()) {
                    Toast.makeText(context, "Введите адрес доставки", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                cartViewModel.confirmOrder(address)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = cartItems.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6A153))
        ) {
            Text("Оформить заказ", color = Color.White)
        }
    }
}