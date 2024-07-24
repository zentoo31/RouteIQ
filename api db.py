import pyodbc
from flask import Flask, jsonify, request

app = Flask(__name__)

conector = pyodbc.connect('DRIVER={SQL Server};SERVER=localhost;DATABASE=ARQUIUCV;UID=sa;PWD=12345678')

@app.route('/')
def index():
    return 'This is the API for RouteIQ app v2'

@app.route('/usuarios', methods=['GET'])
def usuarios():
    if request.method == 'GET':
        cursor = conector.cursor()
        cursor.execute('SELECT * FROM ALUMNOS')
        usuarios = []
        for row in cursor.fetchall():
            usuario = {'usuario': row[0].strip(), 'contrasena': row[1], 'apellido': row[2], 'nombre': row[3], 'correo': row[4]}
            usuarios.append(usuario)
        cursor.close()
        return jsonify(usuarios)

@app.route('/usuarios/<string:usuario_id>', methods=['GET'])
def obtener_usuario(usuario_id):
    cursor = conector.cursor()
    cursor.execute('SELECT * FROM ALUMNOS WHERE USUARIO = ?', (usuario_id,))
    usuario = cursor.fetchone()
    cursor.close()
    if usuario:
        usuario_dict = {'usuario': usuario[0].strip(), 'contrasena': usuario[1], 'apellido': usuario[2], 'nombre': usuario[3], 'correo': usuario[4]}
        return jsonify(usuario_dict)
    else:
        return 'User no encontrado', 404

@app.route('/usuarios', methods=['POST'])
def agregar_usuario():
    if request.method == 'POST':
        nuevo_usuario = request.json
        usuario = nuevo_usuario.get('usuario')
        contrasena = nuevo_usuario.get('contrasena')
        apellido = nuevo_usuario.get('apellido')
        nombre = nuevo_usuario.get('nombre')
        correo = nuevo_usuario.get('correo')

        if not (usuario and contrasena and apellido and nombre and correo):
            return 'Datos incompletos', 400

        try:
            cursor = conector.cursor()
            cursor.execute("INSERT INTO ALUMNOS(USUARIO, CONTRASENA, APELLIDO, NOMBRE, CORREO) VALUES (?, ?, ?, ?, ?)",
                           (usuario.strip(), contrasena, apellido, nombre, correo))
            conector.commit()
            cursor.close()
            return 'Usuario creado correctamente!', 201
        except Exception as e:
            return str(e), 500

@app.route('/pagar', methods=['POST'])
def realizar_pago():
    if request.method == 'POST':
        data = request.json
        usuario = data.get('Usuario')
        bus = data.get('Bus')
        monto = data.get('Monto')
        descripcion = data.get('Descripcion')

        # Log de los datos recibidos
        print(f"Datos recibidos: usuario={usuario}, bus={bus}, monto={monto}, descripcion={descripcion}")

        if not (usuario and bus and monto and descripcion):
            return 'Datos incompletos', 400

        try:
            cursor = conector.cursor()
            cursor.execute("EXEC sp_realizarPago @usuario=?, @bus=?, @monto=?, @descripcion=?", 
                           (usuario, bus, monto, descripcion))
            conector.commit()
            cursor.close()
            return 'Pago realizado correctamente!', 200
        except Exception as e:
            return str(e), 500




@app.route('/deposito', methods=['POST'])
def realizar_deposito():
    if request.method == 'POST':
        datos = request.json
        usuario = datos.get('usuario')
        monto = datos.get('monto')
        descripcion = datos.get('descripcion')
        bus = datos.get('bus')

        if not (usuario and monto and descripcion and bus):
            return 'Datos incompletos', 400

        try:
            cursor = conector.cursor()
            cursor.execute("EXEC sp_realizarDeposito ?, ?, ?, ?", (usuario, monto, descripcion, bus))
            resultado = cursor.fetchone()
            conector.commit()
            cursor.close()
            return jsonify({'mensaje': resultado[0]})
        except Exception as e:
            return str(e), 500

@app.route('/transacciones/<string:usuario>', methods=['GET'])
def obtener_transacciones(usuario):
    try:
        cursor = conector.cursor()
        cursor.execute("EXEC sp_obtenerTransacciones ?", (usuario,))
        transacciones = []
        for row in cursor.fetchall():
            transaccion = {
                'transaccionID': row[0],
                'billeteraID': row[1],
                'bus': row[2],
                'tipoTransaccion': row[3],
                'monto': row[4],
                'fechaTransaccion': row[5],
                'descripcion': row[6]
            }
            transacciones.append(transaccion)
        cursor.close()
        return jsonify(transacciones)
    except Exception as e:
        return str(e), 500

@app.route('/saldo/<string:usuario>', methods=['GET'])
def obtener_saldo(usuario):
    try:
        cursor = conector.cursor()
        cursor.execute("EXEC sp_obtenerSaldo ?", (usuario,))
        saldo = cursor.fetchone()
        cursor.close()
        if saldo:
            return jsonify({'usuario': usuario, 'saldo': saldo[0]})
        else:
            return 'Usuario no encontrado', 404
    except Exception as e:
        return str(e), 500


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
