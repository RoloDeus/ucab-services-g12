// ==========================================
// 1. LÓGICA DE REGISTRO
// ==========================================
const formRegistro = document.getElementById('formRegistro');

if (formRegistro) {
    formRegistro.addEventListener('submit', function(e) {
        e.preventDefault(); // Evita que la página se recargue al darle al botón

        // Armamos el objeto exactamente como lo definimos en el DTO de Spring Boot
        const payload = {
            usuario: {
                cedula: document.getElementById('cedula').value,
                nombres: document.getElementById('nombres').value,
                apellidos: document.getElementById('apellidos').value,
                fechaNacimiento: document.getElementById('fechaNacimiento').value,
                genero: document.getElementById('genero').value,
                calle: document.getElementById('calle').value,
                zona: document.getElementById('zona').value,
                ciudad: document.getElementById('ciudad').value,
                telefono: document.getElementById('telefono').value,
                correoInstitucional: document.getElementById('correoRegistro').value,
                contrasena: document.getElementById('contrasenaRegistro').value
            },
            // Capturamos el rol del select (Estudiante o Profesor)
            rolSeleccionado: document.getElementById('rolSeleccionado').value 
        };

        // Enviamos los datos al backend
        fetch('http://localhost:8080/api/auth/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                alert('¡Registro exitoso! Ya puedes iniciar sesión.');
                // Lo enviamos a la pantalla de login para que ingrese
                window.location.href = '/login'; 
            } else {
                alert('Error en el registro: ' + data.mensaje);
            }
        })
        .catch(error => console.error('Error:', error));
    });
}


// ==========================================
// 2. LÓGICA DE INICIO DE SESIÓN (LOGIN)
// ==========================================
const formLogin = document.getElementById('formLogin');

if (formLogin) {
    formLogin.addEventListener('submit', function(e) {
        e.preventDefault();

        const correo = document.getElementById('correoLogin').value;
        const contrasena = document.getElementById('contrasenaLogin').value;

        fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ correo: correo, contrasena: contrasena })
        })
        .then(response => response.json())
        .then(data => {
            if (data.mensaje === 'Login Exitoso') {
                // ¡AQUÍ ESTÁ LA MAGIA! Si el login es correcto, lo enviamos al Panel Principal (Dashboard)
                window.location.href = '/inicio?correo=' + encodeURIComponent(correo);
            } else {
                // Si la clave es incorrecta, la cuenta está bloqueada o suspendida, mostramos el mensaje
                alert(data.mensaje); 
            }
        })
        .catch(error => console.error('Error:', error));
    });
}