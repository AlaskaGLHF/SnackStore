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
import androidx.compose.ui.Alignment
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
import android.util.Log

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snackstore.ViewModels.CartViewModel
import com.example.snackstore.ViewModels.EditProfileViewModel
import com.example.snackstore.ViewModels.GoodsViewModel
import com.example.snackstore.ViewModels.GoodsViewModelFactory
import com.example.snackstore.entity.Goods
import kotlinx.coroutines.launch
import java.util.Date


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("cart") { CartScreen(navController) }
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
                    if (login.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        Log.w("SnackStore", "Попытка входа с пустыми полями")
                    } else {
                        Log.d("SnackStore", "Попытка входа: $login")
                        viewModel.login(email = login, password = password)
                    }
                },
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
}

@Composable
fun RegisterScreen(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                        Log.w("SnackStore", "Попытка регистрации с неполными данными")
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
fun MainScreen(
    navController: NavHostController,
    goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application))
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
                ProductGrid(
                    goodsList = goodsList,
                    favorites = favorites,
                    onToggleFavorite = { goodsViewModel.toggleFavorite(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun BannerSlider(modifier: Modifier = Modifier) {
    val images = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3
    )

    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000L)
            currentPage = (currentPage + 1) % images.size
        }
    }

    Image(
        painter = painterResource(id = images[currentPage]),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
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
        Icon(imageVector = Icons.Filled.Menu, contentDescription = null, tint = Color.White)
        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White)
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
fun ProductGrid(
    modifier: Modifier = Modifier,
    goodsList: List<Goods>,
    favorites: Set<Int>,
    onToggleFavorite: (Goods) -> Unit,
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
                        isInitiallyLiked = favorites.contains(good.id),
                        modifier = Modifier.weight(1f),
                        onToggleFavorite = { onToggleFavorite(it) }
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
    isInitiallyLiked: Boolean = false,
    onAddToCart: (Goods) -> Unit = {},
    onToggleFavorite: (Goods) -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(isInitiallyLiked) }

    Column(
        modifier = modifier
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .width(IntrinsicSize.Min) // чтобы карточка не растягивалась больше нужного
    )  {
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
                        isLiked = !isLiked
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
                onClick = { onAddToCart(good) },
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

@SuppressLint("DiscouragedApi")
@Composable
fun getImageResByName(imageName: String?): Int {
    val context = LocalContext.current
    if (imageName.isNullOrEmpty()) return R.drawable.fool
    val resourceName = imageName.substringBeforeLast('.')
    return context.resources.getIdentifier(resourceName, "drawable", context.packageName).takeIf { it != 0 } ?: R.drawable.fool
}



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
        // Header
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

        // Tabs
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

        // Content
        when (selectedTabIndex) {
            0 -> FavoriteList()
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
        Log.d("EditProfileScreen", "Пробуем прочитать client после задержки: ${client}")
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
fun FavoriteList(goodsViewModel: GoodsViewModel = viewModel(factory = GoodsViewModelFactory(LocalContext.current.applicationContext as Application))) {
    val favorites by goodsViewModel.favoriteGoodsList.collectAsState()

    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(favorites) { good ->
            ProductCard(good = good)
        }
    }
}

@Composable
fun OrdersList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(5) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD6A153) // Устанавливаем нужный фон
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Дата заказа: 0${index + 1}.06.2025",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Сумма: ${(index + 1) * 1000} ₽",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(3) { imgIndex ->
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Фото ${imgIndex + 1}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cartItems by cartViewModel
        .cartDao
        .getDetailedCartItems(cartViewModel.clientId)
        .collectAsState(initial = emptyList())

    var address by remember { mutableStateOf("") }
    val date = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date())

    val totalPrice = cartItems.sumOf {
        val price = it.price?.toIntOrNull() ?: 0
        price * it.item.quantity
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFD6A153))
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
            )
        }

        // Icon
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Корзина",
            tint = Color(0xFFD6A153),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )

        // Cart content box
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color(0xFFF5F1E6), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                // Date and total price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = date, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "$totalPrice руб", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Address field
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = { Text("Поле адреса") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFFD6A153),
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cart items
                cartItems.forEach {
                    OrderItem(
                        name = it.name ?: "Товар",
                        price = "${it.price} руб x${it.item.quantity}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Confirm button
        Button(
            onClick = {
                scope.launch {
                    cartViewModel.confirmOrder()  // БД-операция
                    Toast.makeText(context, "Ваш заказ успешно оформлен", Toast.LENGTH_SHORT).show()
                    navController.navigate("main") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6A153))
        ) {
            Text(text = "Подтвердить заказ", color = Color.White)
        }
    }
}




@Composable
fun OrderItem(name: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
        }

        Text(text = price, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
    }
}