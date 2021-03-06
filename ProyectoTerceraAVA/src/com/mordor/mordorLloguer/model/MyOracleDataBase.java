package com.mordor.mordorLloguer.model;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

public class MyOracleDataBase implements AlmacenDatosDB {
	/**
	 * Método que recoge a los empleados de la base de datos
	 * @param where condición que se le pone a esta recogida de datos
	 * @param order el orden de la recogida de datos
	 * @return devuelve los empleados
	 */
	private ArrayList<Empleado> getCustomEmpleados(String where, String order) {
		
		ArrayList<Empleado> empleados = new ArrayList<Empleado>();
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "SELECT * FROM EMPLEADO ";
		if(where != null) 
			query += "WHERE " + where;
		if(order != null) {
			query += "ORDER BY " + order;
		}
		
		
		
		
		try(Connection con = ds.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);) {
			
			Empleado empleado;
			
			while(rs.next()) {
				
				empleado = new Empleado(
										rs.getString("DNI"),
										rs.getString("nombre"),
										rs.getString("apellidos"),
										rs.getString("CP"),
										rs.getString("email"),
										rs.getDate("fechaNac"),
										rs.getString("cargo"),
										rs.getString("domicilio"),
										rs.getString("password"));	
		
				empleados.add(empleado);
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return empleados;
		
		
	}
	/**
	 * Devuelve a los empleados por defecto
	 */
	@Override
	public ArrayList<Empleado> getEmpleados() {
		
		return getCustomEmpleados(null,null);

	}
	/**
	 * Devuelve los empleados ordenados por código postal
	 */
	@Override
	public ArrayList<Empleado> getEmpleadoPorCP(String cp) {
	
		return getCustomEmpleados("CP=" + cp,null);
	}
	/**
	 * Devuelve los empleados ordenados por cargo
	 */
	@Override
	public ArrayList<Empleado> getEmpleadoPorCargo(String cargo) {
		
		return getCustomEmpleados("cargo='" +cargo + "'",null);
	}
	/**
	 * Devuelve los empleados ordenador por dni
	 */
	@Override
	public Empleado getEmpleadoPorDNI(String DNI) {
		
		ArrayList<Empleado> empleado = getCustomEmpleados("dni='"+DNI+"'",null);
		if(empleado.size() != 0)
			return empleado.get(0);
		else
			return null;
		
	}
	/**
	 * Devuelve verdadero o falso si el empleado se ha actualizado correctamente
	 */
	@Override
	public boolean updateEmpleado(Empleado empleado) {

	boolean actualizado = false;

	DataSource ds = MyDataSource.getOracleDataSource();

	try (Connection con = ds.getConnection();

	Statement stmt = con.createStatement();) {

	String query = "UPDATE EMPLEADO SET nombre='"+empleado.getNombre()+"', "+ 
					"apellidos='"+empleado.getApellidos()+"',"+ 
					((empleado.getDomicilio() != null)?"domicilio='"+empleado.getDomicilio()+"',":"")+ 
					((empleado.getCP() != null)?"CP='"+empleado.getCP()+"',":"")+ 
					"email='"+empleado.getEmail()+"',"+ 
					"fechaNac=TO_DATE('" +empleado.getFechaNac()+"','yyyy-mm-dd'),"+ 
					"cargo='"+empleado.getCargo()+"' "+ 
					"WHERE DNI='" + empleado.getDNI() +"'";

	
	stmt.executeUpdate(query);

	actualizado = (stmt.executeUpdate(query)==1)?true:false;

	} catch (SQLException e) {

	e.printStackTrace();

	}

	return actualizado;

	}
	/**
	 * Devuelve verdadero o falso si el empleado se ha borrado correctamente
	 */
	@Override
	public boolean deleteEmpleado(String DNI) {
		
		boolean actualizado = false;
		DataSource ds = MyDataSource.getOracleDataSource();
		String query = "DELETE FROM EMPLEADO WHERE DNI = ?";
		
		try (Connection con = ds.getConnection();
				PreparedStatement stmt = con.prepareStatement(query);) {
			
			
			
			
		
			stmt.setString(1, DNI);
			
			
			actualizado = (stmt.executeUpdate()==1)?true:false;
			

			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	
		
		return actualizado;
	}
	/**
	 * Método que devuelve verdadero o falso si el usuario a conseguido registrarse
	 */
	@Override
	public boolean authenticate(String login, String password) {
		
		boolean registrado = false;
		DataSource ds = MyDataSource.getOracleDataSource();
		String query = "Select count(*) FROM EMPLEADO WHERE DNI=? and password=ENCRYPT_PASWD.encrypt_val(?)";
		
		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(query);) {
			
			int pos=0;
			pstmt.setString(++pos, login);
			pstmt.setString(++pos, password);
			
			ResultSet rs = pstmt.executeQuery();
			
			rs.next();
			if(rs.getInt(1) == 1) {
				registrado = true;
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		
		return registrado;
	}
	/**
	 * Método que devuelve los empleados un orden concreto
	 * @param ad el orden por el que ordenaremos la tabla
	 */
	@Override
	public ArrayList<Empleado> getEmpleadosOrder(String order, int ad) {
		ArrayList<Empleado> empleados = new ArrayList<>();
		
		if (ad == AlmacenDatosDB.ASCENDENTE) {
			empleados = getCustomEmpleados(null,order); 
		} else {
			empleados = getCustomEmpleados(null,order + " desc"); 
		}
		
		return empleados;
	}
	/**
	 * Método que devuelve verdadero o falso según si el empleado se ha insertado correctamente
	 */
	@Override
	public boolean insertEmpleado(Empleado empleado) throws Exception {
		
		boolean insertado = false;

		DataSource ds = MyDataSource.getOracleDataSource();

		String query = "INSERT INTO EMPLEADO (DNI,NOMBRE,APELLIDOS,DOMICILIO,CP,EMAIL,FECHANAC,CARGO,PASSWORD) VALUES (?,?,?,?,?,?,?,?,ENCRYPT_PASWD.encrypt_val(?))" ;
		
		try (Connection con = ds.getConnection();

		PreparedStatement pstmt = con.prepareStatement(query);) {

		int pos = 0;
		pstmt.setString(++pos, empleado.getDNI());
		pstmt.setString(++pos, empleado.getNombre());
		pstmt.setString(++pos, empleado.getApellidos());
		pstmt.setString(++pos, empleado.getDomicilio());
		pstmt.setString(++pos, empleado.getCP());
		pstmt.setString(++pos, empleado.getEmail());
		pstmt.setDate(++pos, empleado.getFechaNac());
		pstmt.setString(++pos, empleado.getCargo());
		pstmt.setString(++pos, empleado.getPassword());

		

		
		
		
		insertado = (pstmt.executeUpdate()==1)?true:false;
		
		
		}
		
		return insertado;
		
		
		
	}
	/**
	 * Método que devuelve una lista con todos los clientes
	 */
	@Override
	public ArrayList<Cliente> getClientes() {
		
		ArrayList<Cliente> clientes = new ArrayList<Cliente>();
		
		ResultSet rs = null;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call ?:=GESTIONALQUILER.LISTARCLIENTES() }";
		
		
		
		
		try(Connection con = ds.getConnection();
			CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			
			cstmt.execute();
			
			rs = (ResultSet) cstmt.getObject(1);
			
			Cliente empleado;
			
			while(rs.next()) {
				
			
				
				empleado = new Cliente(
										rs.getInt("idcliente"),
										rs.getString("DNI"),
										rs.getString("nombre"),
										rs.getString("apellidos"),
										rs.getString("domicilio"),
										rs.getString("cp"),
										rs.getString("email"),
										rs.getDate("fechanac"),
										rs.getString("carnet").charAt(0),
										rs.getBytes("foto"));	
		
				clientes.add(empleado);
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return clientes;
		
		
		
	}
	/**
	 * Método que devuelve verdadero o falso según si se borra correctamente el cliente
	 */
	@Override
	public boolean deleteCustomer(String DNI) {
		
		boolean borrado = false;
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONALQUILER.BORRARCLIENTE(?) }";
		
		
		
		
		try(Connection con = ds.getConnection();
			CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, DNI);
			
			borrado = (cstmt.executeUpdate() == 1)?true:false;
			
		
		
		
		
	} catch (SQLException e) {
		
		
		e.printStackTrace();
	}
		return borrado;
	
	}
	/**
	 * Método que devuelve verdadero o falso según si el cliente se ha grabado correctamente
	 */
	@Override
	public boolean grabarCliente(Cliente cliente) {
		
		boolean grabado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONALQUILER.GRABARCLIENTE(?,?,?,?,?,?,?,?,?)}";
		
		try(Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, cliente.getDNI());
			cstmt.setString(2, cliente.getNombre());
			cstmt.setString(3, cliente.getApellidos());
			cstmt.setString(4, cliente.getDomicilio());
			cstmt.setString(5, cliente.getCp());
			cstmt.setString(6, cliente.getEmail());
			cstmt.setDate(7, cliente.getFechaNac());
			cstmt.setString(8, String.valueOf(cliente.getCarnet()));
			cstmt.setBytes(9, cliente.getFoto());
			
			grabado = (cstmt.executeUpdate() == 1)?true:false;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return grabado;
	}

	@Override
	public boolean updateCustomer(Cliente cliente) {
		
		return grabarCliente(cliente);
	}

	/**
	 * Método que recoge todos los vehículos
	 * @param tipo Parámetro que indica el tipo de vehículo
	 * @return Devuelve un array de vehículos
	 * @throws SQLException
	 * @throws ParseException
	 */
	private ArrayList<?> getVehiculos(String tipo) throws SQLException, ParseException {
		
		ArrayList<Vehiculo> vehiculos = new ArrayList<>();
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		ResultSet rs = null;
		
		String query = "{ call GESTIONVEHICULOS.listarvehiculos(?,?)}";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, tipo);
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);
			cstmt.execute();
			
			rs = (ResultSet) cstmt.getObject(2);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			
			while(rs.next()) {
				
				String matricula = rs.getString("c1");
				Double preciodia = rs.getDouble("n1");
				String marca = rs.getString("c2");
				String descripcion = rs.getString("c3");
				String color = rs.getString("c4");
				String motor = rs.getString("c5");
				Double cilindrada = rs.getDouble("n2");
				Date fechaadq = new Date(format.parse(rs.getString("c6")).getTime());
				String estado = rs.getString("c7");
				char carnet = rs.getString("c8").charAt(0);
				
				if(tipo.equals(CAR)) {
					
					int numplazas = rs.getInt("n3");
					int numpuertas = rs.getInt("n4");
					
					vehiculos.add(new Coche(matricula, preciodia, marca, descripcion, color
							,motor,cilindrada,fechaadq,estado,carnet,numplazas,numpuertas));
					
				} else if(tipo.equals(TRUCK)) {
					
					int numruedas = rs.getInt("n3");
					double mma = rs.getDouble("n4");
					

					vehiculos.add(new Camion(matricula, preciodia, marca, descripcion, color
							,motor,cilindrada,fechaadq,estado,carnet,numruedas,mma));
					
					
				} else if(tipo.equals(VAN)) {
					
					double mma = rs.getDouble("n3");
					

					vehiculos.add(new Furgoneta(matricula, preciodia, marca, descripcion, color
							,motor,cilindrada,fechaadq,estado,carnet,mma));
					
					
				} else if(tipo.equals(MINIBUS)) {
					
					int numplazas = rs.getInt("n3");
					int medida = rs.getInt("n4");
					

					vehiculos.add(new Microbus(matricula, preciodia, marca, descripcion, color
							,motor,cilindrada,fechaadq,estado,carnet,numplazas,medida));
					
					
				}
				
			}
			
		}
		
		if(rs != null) {
			rs.close();
		}
		
		
		
		return vehiculos;
	}
	/**
	 * Método que rellena el array de coches
	 */
	@Override
	public ArrayList<Coche> getCoche() throws SQLException, ParseException {
		
		
		
		return (ArrayList<Coche>) getVehiculos(CAR);
	}
	/**
	 * Método que rellena el array de camiones
	 */
	@Override
	public ArrayList<Camion> getCamion() throws SQLException, ParseException {
		
		return (ArrayList<Camion>) getVehiculos(TRUCK);
	}
	/**
	 * Método que rellena el array de furgoneta
	 */
	@Override
	public ArrayList<Furgoneta> getFurgoneta() throws SQLException, ParseException {
		
		return (ArrayList<Furgoneta>) getVehiculos(VAN);
	}
	/**
	 * Método que rellena el array de micro buses
	 */
	@Override
	public ArrayList<Microbus> getMicroBus() throws SQLException, ParseException {
		
		return (ArrayList<Microbus>) getVehiculos(MINIBUS);
	}
	/**
	 * Método que borra los vehículos
	 * @param tipo Parámetro que indica el tipo de vehículo a borrar
	 * @param matricula matrícula del vehículo a borrar
	 * @return
	 * @throws SQLException
	 */
	private boolean deleteVehicle(String tipo, String matricula) throws SQLException {
		
		boolean borrado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "";
		
		if(tipo.equals(CAR)) {
			 query = "{ call GESTIONVEHICULOS.borrarCoche(?)}";
		} else if(tipo.equals(VAN)) {
			 query = "{ call GESTIONVEHICULOS.borrarFurgoneta(?)}";
		} else if(tipo.equals(TRUCK)) {
			 query = "{ call GESTIONVEHICULOS.borrarCamion(?)}";
		} else if(tipo.equals(MINIBUS)) {
			 query = "{ call GESTIONVEHICULOS.borrarMicrobus(?)}";
		}
		
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, matricula);
			
			borrado = (cstmt.executeUpdate() == 1)? true:false;
			
		}
		
		
		return borrado;
		
	}

	@Override
	public boolean deleteCar(String matricula) throws SQLException {
		
		return deleteVehicle(CAR,matricula);
		
	}

	@Override
	public boolean deleteTruck(String matricula) throws SQLException {
		
		return deleteVehicle(TRUCK,matricula);
	}

	@Override
	public boolean deleteVan(String matricula) throws SQLException {
		
		return deleteVehicle(VAN,matricula);
	}

	@Override
	public boolean deleteMinibus(String matricula) throws SQLException {
		
		return deleteVehicle(MINIBUS,matricula);
	}
	/**
	 * Método que se encarga de actualizar el coche
	 */
	@Override
	public boolean updateCar(Coche c) throws SQLException {
		
		boolean update = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.actualizarCoche(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, c.getMatricula());
			cstmt.setDouble(2, c.getPreciodia());
			cstmt.setString(3, c.getMarca());
			cstmt.setString(4, c.getDescripcion());
			cstmt.setString(5, c.getColor());
			cstmt.setString(6, c.getMotor());
			cstmt.setDouble(7, c.getCilindrada());
			cstmt.setDate(8, c.getFechaadq());
			cstmt.setString(9, c.getEstado());
			cstmt.setString(10, String.valueOf(c.getCarnet()));
			cstmt.setInt(11, c.getNumplazas());
			cstmt.setInt(12, c.getNumpuertas());
			
			update = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return update;
	}
	/**
	 * Método que se encarga de actualizar el camión
	 */
	@Override
	public boolean updateTruck(Camion c) throws SQLException {
		
		boolean update = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.actualizarCamion(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, c.getMatricula());
			cstmt.setDouble(2, c.getPreciodia());
			cstmt.setString(3, c.getMarca());
			cstmt.setString(4, c.getDescripcion());
			cstmt.setString(5, c.getColor());
			cstmt.setString(6, c.getMotor());
			cstmt.setDouble(7, c.getCilindrada());
			cstmt.setDate(8, c.getFechaadq());
			cstmt.setString(9, c.getEstado());
			cstmt.setString(10, String.valueOf(c.getCarnet()));
			cstmt.setInt(11, c.getNumruedas());
			cstmt.setDouble(12, c.getMma());
			
			update = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return update;
		
		
		
	}
	/**
	 * Método que se encarga de actualiza la furgoneta
	 */
	@Override
	public boolean updateVan(Furgoneta f) throws SQLException {
		
		boolean update = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.actualizarFurgoneta(?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, f.getMatricula());
			cstmt.setDouble(2, f.getPreciodia());
			cstmt.setString(3, f.getMarca());
			cstmt.setString(4, f.getDescripcion());
			cstmt.setString(5, f.getColor());
			cstmt.setString(6, f.getMotor());
			cstmt.setDouble(7, f.getCilindrada());
			cstmt.setDate(8, f.getFechaadq());
			cstmt.setString(9, f.getEstado());
			cstmt.setString(10, String.valueOf(f.getCarnet()));
			cstmt.setDouble(11, f.getMma());
			
			
			update = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return update;
	}
	/**
	 * Método que se encarga de actualizar el microbus
	 */
	@Override
	public boolean updateMinibus(Microbus m) throws SQLException {
		boolean update = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.actualizarMicrobus(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, m.getMatricula());
			cstmt.setDouble(2, m.getPreciodia());
			cstmt.setString(3, m.getMarca());
			cstmt.setString(4, m.getDescripcion());
			cstmt.setString(5, m.getColor());
			cstmt.setString(6, m.getMotor());
			cstmt.setDouble(7, m.getCilindrada());
			cstmt.setDate(8, m.getFechaadq());
			cstmt.setString(9, m.getEstado());
			cstmt.setString(10, String.valueOf(m.getCarnet()));
			cstmt.setInt(11, m.getNumplazas());
			cstmt.setDouble(12, m.getMedida());
			
			update = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return update;
	}
	/**
	 * Método que anyade un coche
	 */
	@Override
	public boolean addCar(Coche c) throws SQLException {
		boolean insertado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.insertarCoche(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, c.getMatricula());
			cstmt.setDouble(2, c.getPreciodia());
			cstmt.setString(3, c.getMarca());
			cstmt.setString(4, c.getDescripcion());
			cstmt.setString(5, c.getColor());
			cstmt.setString(6, c.getMotor());
			if(c.getCilindrada() == null) {
				cstmt.setNull(7, OracleTypes.NULL);
			} else {
				cstmt.setDouble(7, c.getCilindrada());
			}
			
			cstmt.setDate(8, c.getFechaadq());
			cstmt.setString(9, c.getEstado());
			cstmt.setString(10, String.valueOf(c.getCarnet()));
			cstmt.setInt(11, c.getNumplazas());
			cstmt.setInt(12, c.getNumpuertas());
			
			insertado = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return insertado;
		
		
		
	}
	/**
	 * Método que anyade un camión
	 */
	@Override
	public boolean addTruck(Camion c) throws SQLException {
	boolean insertado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.insertarCamion(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, c.getMatricula());
			cstmt.setDouble(2, c.getPreciodia());
			cstmt.setString(3, c.getMarca());
			cstmt.setString(4, c.getDescripcion());
			cstmt.setString(5, c.getColor());
			cstmt.setString(6, c.getMotor());
			if(c.getCilindrada() == null) {
				cstmt.setNull(7, OracleTypes.NULL);
			} else {
				cstmt.setDouble(7, c.getCilindrada());
			}
			cstmt.setDate(8, c.getFechaadq());
			cstmt.setString(9, c.getEstado());
			cstmt.setString(10, String.valueOf(c.getCarnet()));
			cstmt.setInt(11, c.getNumruedas());
			cstmt.setDouble(12, c.getMma());
			
			insertado = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return insertado;
	}
	/**
	 * Método que anyade una furgoneta
	 */
	@Override
	public boolean addVan(Furgoneta f) throws SQLException {
	boolean insertado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.insertarFurgoneta(?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, f.getMatricula());
			cstmt.setDouble(2, f.getPreciodia());
			cstmt.setString(3, f.getMarca());
			cstmt.setString(4, f.getDescripcion());
			cstmt.setString(5, f.getColor());
			cstmt.setString(6, f.getMotor());
			if(f.getCilindrada() == null) {
				cstmt.setNull(7, OracleTypes.NULL);
			} else {
				cstmt.setDouble(7, f.getCilindrada());
			}
			
			cstmt.setDate(8, f.getFechaadq());
			cstmt.setString(9, f.getEstado());
			cstmt.setString(10, String.valueOf(f.getCarnet()));
			cstmt.setDouble(11, f.getMma());
			
			
			insertado = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return insertado;
	}
	/**
	 * Método que anyade un microbus
	 */
	@Override
	public boolean addMinibus(Microbus m) throws SQLException {
	boolean insertado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONVEHICULOS.insertarMicrobus(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setString(1, m.getMatricula());
			cstmt.setDouble(2, m.getPreciodia());
			cstmt.setString(3, m.getMarca());
			cstmt.setString(4, m.getDescripcion());
			cstmt.setString(5, m.getColor());
			cstmt.setString(6, m.getMotor());
			if(m.getCilindrada() == null) {
				cstmt.setNull(7, OracleTypes.NULL);
			} else {
				cstmt.setDouble(7, m.getCilindrada());
			}
			
			cstmt.setDate(8, m.getFechaadq());
			cstmt.setString(9, m.getEstado());
			cstmt.setString(10, String.valueOf(m.getCarnet()));
			cstmt.setInt(11, m.getNumplazas());
			cstmt.setDouble(12, m.getMedida());
			
			insertado = (cstmt.executeUpdate() == 1)? true:false;
		} 
		
		return insertado;
	}
	/**
	 * Método que recoge todas las facturas
	 */
	@Override
	public ArrayList<Factura> getFacturas() throws SQLException {
		
		ArrayList<Factura> facturas = new ArrayList<Factura>();
		
		ResultSet rs = null;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call ?:=GESTIONALQUILER.LISTARFACTURAS() }";
		
		
		
		
		try(Connection con = ds.getConnection();
			CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			
			cstmt.execute();
			
			rs = (ResultSet) cstmt.getObject(1);
			
			Factura empleado;
			
			while(rs.next()) {
				
			
				
				empleado = new Factura(
										rs.getInt("idfactura"),
										rs.getDate("fecha"),
										rs.getDouble("importebase"),
										rs.getDouble("importeiva"),
										rs.getInt("clienteid"));
		
				facturas.add(empleado);
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return facturas;
		
	}
	/**
	 * Método que devuelve todos los alquileres
	 */
	@Override
	public ArrayList<Alquiler> getAlquiler() throws SQLException {
		ArrayList<Alquiler> facturas = new ArrayList<Alquiler>();
		
		ResultSet rs = null;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call ?:=GESTIONALQUILER.LISTARALQUILERES() }";
		
		
		
		
		try(Connection con = ds.getConnection();
			CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			
			cstmt.execute();
			
			rs = (ResultSet) cstmt.getObject(1);
			
			Alquiler empleado;
			
			while(rs.next()) {
				
			
				
				empleado = new Alquiler(
										rs.getInt("idalquiler"),
										rs.getInt("idfactura"),
										rs.getString("matricula"),
										rs.getDate("fechainicio"),
										rs.getDate("fechafin"),
										rs.getDouble("precio"));
		
				facturas.add(empleado);
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return facturas;
	}
	/**
	 * Método que inserta alquileres
	 */
	@Override
	public boolean insertarAlquiler(Integer idfactura, String DNI, String matricula, Date fechainicio, Date fechafinal) throws SQLException {
		
		boolean insertado = true;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call ?:=GESTIONALQUILER.INSERTARALQUILER(?,?,?,?,?,?)}";
		
		try(Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setInt(1, idfactura);
			cstmt.setInt(2, idfactura);
			cstmt.setString(3, DNI);
			cstmt.setString(4, matricula);
			cstmt.setDate(5, fechainicio);
			cstmt.setDate(6, fechafinal);
			cstmt.registerOutParameter(7, OracleTypes.INTEGER);
			
			insertado = (cstmt.executeUpdate() == 1)? true:false;
			
		}
		
		return insertado;
	}
	/**
	 * Método que inserta una factura
	 */
	@Override
	public boolean insertarFactura(String DNI, String matricula, Date fechainicio, Date fechafinal) throws SQLException {
		
		boolean insertado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call ?:=GESTIONALQUILER.INSERTARALQUILER(?,?,?,?,?,?)}";
		
		try(Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.registerOutParameter(1, OracleTypes.INTEGER);
			cstmt.setObject(2, null);
			cstmt.setString(3, DNI);
			cstmt.setString(4, matricula);
			cstmt.setDate(5, fechainicio);
			cstmt.setDate(6, fechafinal);
			cstmt.setObject(7, null);
			
			insertado = (cstmt.executeUpdate() == 1)? true:false;
			
		}
		
		return insertado;
	}
	/**
	 * Método que borra un alquiler
	 */
	@Override
	public boolean borrarAlquiler(Integer idfactura) throws SQLException {
		
		boolean borrado = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONALQUILER.BORRARALQUILER(?)}";
		
		try(Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setInt(1, idfactura);

			
			borrado = (cstmt.executeUpdate() == 1)? true:false;
			
		}
		return borrado;
	}
	/**
	 * Método que actualiza un alquiler
	 */
	@Override
	public boolean updateAlquiler(Integer idfactura, Date fechainicio, Date fechafinal) throws SQLException {
		
		boolean update = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONALQUILER.MODIFICARALQUILER(?,?,?)}";
		
		try(Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setInt(1, idfactura);
			cstmt.setDate(2, fechainicio);
			cstmt.setDate(3, fechafinal);
	
			
			update = (cstmt.executeUpdate() == 1)? true:false;
		}
		
		return update;
	}
	/**
	 * Método que comprueba si la factura ha acabado
	 */
	@Override
	public boolean checkVehicle(Integer idfactura) throws SQLException {
		
		boolean check = false;
		
		DataSource ds = MyDataSource.getOracleDataSource();
		
		String query = "{ call GESTIONALQUILER.FACTURACOBRADA(?)}";
		
		try(Connection con = ds.getConnection();
				CallableStatement cstmt = con.prepareCall(query);) {
			
			cstmt.setInt(1, idfactura);
			
			check = (cstmt.executeUpdate() == 1)? true:false;
			
		}
		
		return false;
	}
	
	
	
	
	
	
}	
