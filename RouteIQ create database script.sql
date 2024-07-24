create database ARQUIUCV
go

use ARQUIUCV
go

create table ARQUIUCV..ALUMNOS
(
	USUARIO varchar(20) not null,
	CONTRASENA char(8) not null,
	APELLIDO varchar(30) not null,
	NOMBRE varchar(30) not null,
	CORREO varchar(50) not null,
	FotoPerfil varchar(50) null,
	FechaRegistro DATETIME DEFAULT GETDATE(),
	constraint pk_usuario primary key(USUARIO)
)
go


CREATE TABLE Billetera (
    BilleteraID INT PRIMARY KEY IDENTITY(1,1),
    USUARIO varchar(20) FOREIGN KEY REFERENCES ALUMNOS(USUARIO),
    Saldo DECIMAL(18, 2) NOT NULL DEFAULT 0.0,  -- Saldo de la billetera
    Moneda NVARCHAR(10) NOT NULL DEFAULT 'PEN', -- Tipo de moneda
    FechaCreacion DATETIME DEFAULT GETDATE(),
    UltimaActualizacion DATETIME DEFAULT GETDATE()
);

CREATE TABLE Transacciones (
    TransaccionID INT PRIMARY KEY IDENTITY(1,1),
    BilleteraID INT FOREIGN KEY REFERENCES Billetera(BilleteraID),
	Bus varchar(10) not null,
    TipoTransaccion NVARCHAR(50) NOT NULL, -- Tipo de transacción (e.g., Depósito, Retiro, Pago)
    Monto DECIMAL(18, 2) NOT NULL,
    FechaTransaccion DATETIME DEFAULT GETDATE(),
    Descripcion NVARCHAR(255)
);


INSERT INTO ALUMNOS(USUARIO, CONTRASENA, APELLIDO, NOMBRE, CORREO, FotoPerfil)
VALUES('zentoo31','12345678','Pineda','Diego','zentoo31@gmail.com','https://imgur.com/rP7JqrT')
go

INSERT INTO Billetera(USUARIO,Saldo)
VALUES('zentoo31',20)
go

select * from Billetera
select * from Transacciones


INSERT INTO ALUMNOS(USUARIO, CONTRASENA, APELLIDO, NOMBRE, CORREO)
VALUES('xd123','12345678','Gonzales','Alex','camello123@gmail.com')
go

select * from ALUMNOS

CREATE PROCEDURE sp_RealizarPago
    @Usuario VARCHAR(20),
    @Monto DECIMAL(18, 2),
    @Descripcion NVARCHAR(255),
	@Bus NVARCHAR(10)
AS
BEGIN
    DECLARE @BilleteraID INT
    DECLARE @SaldoActual DECIMAL(18, 2)

    -- Obtener el ID de la billetera y el saldo actual
    SELECT @BilleteraID = BilleteraID, @SaldoActual = Saldo
    FROM Billetera
    WHERE USUARIO = @Usuario

    -- Verificar si la billetera existe
    IF @BilleteraID IS NULL
    BEGIN
        RAISERROR('Billetera no encontrada para el usuario especificado.', 16, 1)
        RETURN
    END

    -- Verificar si hay saldo suficiente
    IF @SaldoActual < @Monto
    BEGIN
        RAISERROR('Saldo insuficiente.', 16, 1)
        RETURN
    END

    -- Actualizar el saldo de la billetera
    UPDATE Billetera
    SET Saldo = Saldo - @Monto,
        UltimaActualizacion = GETDATE()
    WHERE BilleteraID = @BilleteraID

    -- Insertar la transacción
    INSERT INTO Transacciones (BilleteraID, TipoTransaccion, Monto, FechaTransaccion, Descripcion, Bus)
    VALUES (@BilleteraID, 'Pago', @Monto, GETDATE(), @Descripcion, @Bus)
END
GO


CREATE PROCEDURE sp_RealizarRetiro
    @Usuario VARCHAR(20),
    @Monto DECIMAL(18, 2),
    @Descripcion NVARCHAR(255),
	@Bus NVARCHAR(10)
AS
BEGIN
    DECLARE @BilleteraID INT
    DECLARE @SaldoActual DECIMAL(18, 2)

    -- Obtener el ID de la billetera y el saldo actual
    SELECT @BilleteraID = BilleteraID, @SaldoActual = Saldo
    FROM Billetera
    WHERE USUARIO = @Usuario

    -- Verificar si la billetera existe
    IF @BilleteraID IS NULL
    BEGIN
        RAISERROR('Billetera no encontrada para el usuario especificado.', 16, 1)
        RETURN
    END

    -- Verificar si hay saldo suficiente
    IF @SaldoActual < @Monto
    BEGIN
        RAISERROR('Saldo insuficiente.', 16, 1)
        RETURN
    END

    -- Actualizar el saldo de la billetera
    UPDATE Billetera
    SET Saldo = Saldo - @Monto,
        UltimaActualizacion = GETDATE()
    WHERE BilleteraID = @BilleteraID

    -- Insertar la transacción
    INSERT INTO Transacciones (BilleteraID, TipoTransaccion, Monto, FechaTransaccion, Descripcion, Bus)
    VALUES (@BilleteraID, 'Retiro', @Monto, GETDATE(), @Descripcion, @Bus)
END
GO

CREATE PROCEDURE sp_realizarDeposito
    @usuario VARCHAR(20),
    @monto DECIMAL(18, 2),
    @descripcion NVARCHAR(255),
    @bus VARCHAR(10)
AS
BEGIN
    DECLARE @billeteraID INT;

    -- Obtener el BilleteraID del usuario
    SELECT @billeteraID = BilleteraID FROM Billetera WHERE USUARIO = @usuario;

    -- Si no existe billetera para el usuario, retornar un error
    IF @billeteraID IS NULL
    BEGIN
        RAISERROR('Billetera no encontrada para el usuario especificado.', 16, 1);
        RETURN;
    END

    -- Actualizar el saldo de la billetera
    UPDATE Billetera
    SET Saldo = Saldo + @monto, UltimaActualizacion = GETDATE()
    WHERE BilleteraID = @billeteraID;

    -- Registrar la transacción
    INSERT INTO Transacciones (BilleteraID, TipoTransaccion, Monto, FechaTransaccion, Descripcion, Bus)
    VALUES (@billeteraID, 'Depósito', @monto, GETDATE(), @descripcion, @bus);

    -- Retornar éxito
    SELECT 'Depósito realizado exitosamente' AS Mensaje;
END
GO


CREATE PROCEDURE sp_obtenerTransacciones
    @usuario VARCHAR(20)
AS
BEGIN
    -- Obtener el BilleteraID del usuario
    DECLARE @billeteraID INT;
    SELECT @billeteraID = BilleteraID FROM Billetera WHERE USUARIO = @usuario;

    -- Si no existe billetera para el usuario, retornar un error
    IF @billeteraID IS NULL
    BEGIN
        RAISERROR('Billetera no encontrada para el usuario especificado.', 16, 1);
        RETURN;
    END

    -- Recuperar las transacciones de la billetera y ordenarlas por fecha en orden descendente
    SELECT TransaccionID, BilleteraID, Bus, TipoTransaccion, Monto, FechaTransaccion, Descripcion
    FROM Transacciones
    WHERE BilleteraID = @billeteraID
    ORDER BY FechaTransaccion DESC;
END
GO


CREATE PROCEDURE sp_obtenerSaldo
    @usuario VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT saldo
    FROM Billetera
    WHERE usuario = @usuario;
END
GO

-- Crear el trigger en SQL Server
CREATE TRIGGER trg_insertar_billetera
ON ALUMNOS
AFTER INSERT
AS
BEGIN
    -- Variables locales
    DECLARE @billetera_id INT;

    -- Insertar una nueva fila en la tabla Billetera
    INSERT INTO Billetera (USUARIO, Saldo, Moneda, FechaCreacion, UltimaActualizacion)
    SELECT inserted.USUARIO, 0.0, 'PEN', GETDATE(), GETDATE()
    FROM inserted;

    -- Obtener el BilleteraID generado
    SET @billetera_id = SCOPE_IDENTITY();

    -- Actualizar la fila en la tabla ALUMNOS con el BilleteraID generado
    UPDATE ALUMNOS
    SET USUARIO = i.USUARIO
    FROM ALUMNOS a
    INNER JOIN inserted i ON a.USUARIO = i.USUARIO;
END;
GO
