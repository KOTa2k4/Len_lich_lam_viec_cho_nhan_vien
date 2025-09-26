package test.unit;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.Assert;
import org.junit.Test;
import dao.DAO;
import dao.StaffDAO;
import model.Staff;

public class RegisterTableDaoTest {
	StaffDAO rd = new StaffDAO();

	@Test
	public void testSearchRoomException1() {
		String key = "xxxxxxxxxx";
		ArrayList<Staff> listRoom = rd.searchRoom(key);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(0, listRoom.size());
		return;
	}

	@Test
	public void testSearchRoomException2() {
		String key = "rn";
		ArrayList<Staff> listRoom = rd.searchRoom(key);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(0, listRoom.size());
		return;
	}

	@Test
	public void testSearchRoomStandard1() {
		String key = "1";
		ArrayList<Staff> listRoom = rd.searchRoom(key);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(6, listRoom.size());
		for (int i = 0; i < listRoom.size(); i++) {
			Assert.assertTrue(listRoom.get(i).getName().toLowerCase().contains(key.toLowerCase()));
		}
		return;
	}

	@Test
	public void testSearchRoomStandard2() {
		String key = "3";
		ArrayList<Staff> listRoom = rd.searchRoom(key);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(2, listRoom.size());
		for (int i = 0; i < listRoom.size(); i++) {
			Assert.assertTrue(listRoom.get(i).getName().toLowerCase().contains(key.toLowerCase()));
		}
		return;
	}

	@Test
	public void testUpdateRoom() {
		Connection con = DAO.con;
		String newType = "test type";
		String newDes = "test des";
		float newPrice = 5f;
		String key = "102";

		try {
			con.setAutoCommit(false);
			ArrayList<Staff> lr = rd.searchRoom(key);

			lr.get(0).setType(newType);
			lr.get(0).setDes(newDes);
			lr.get(0).setPrice(newPrice);
			rd.updateRoom(lr.get(0));

			// test the new updated row
			lr.clear();
			lr = rd.searchRoom(key);
			Assert.assertTrue(lr.get(0).getName().contains(key));
			Assert.assertEquals(newType, lr.get(0).getType());
			Assert.assertEquals(newPrice, lr.get(0).getPrice(), 0.000001f);
			Assert.assertEquals(newDes, lr.get(0).getDes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.rollback();
				con.setAutoCommit(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return;
	}

	@Test
	public void testSearchFreeRoom1() {
		Date checkin = new GregorianCalendar(2020, Calendar.APRIL, 28).getTime();
		Date checkout = new GregorianCalendar(2020, Calendar.APRIL, 30).getTime();
		ArrayList<Staff> listRoom = rd.searchFreeRoom(checkin, checkout);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(10, listRoom.size());
	}

	@Test
	public void testSearchFreeRoom2() {
		Date checkin = new GregorianCalendar(2020, Calendar.APRIL, 28).getTime();
		Date checkout = new GregorianCalendar(2020, Calendar.MAY, 1).getTime();
		ArrayList<Staff> listRoom = rd.searchFreeRoom(checkin, checkout);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(7, listRoom.size());
		for (int i = 0; i < listRoom.size(); i++) {
			Assert.assertFalse(listRoom.get(i).getId() == 1);
			Assert.assertFalse(listRoom.get(i).getId() == 3);
			Assert.assertFalse(listRoom.get(i).getId() == 5);
		}
	}

	@Test
	public void testSearchFreeRoom3() {
		Date checkin = new GregorianCalendar(2020, Calendar.MAY, 1).getTime();
		Date checkout = new GregorianCalendar(2020, Calendar.MAY, 6).getTime();
		ArrayList<Staff> listRoom = rd.searchFreeRoom(checkin, checkout);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(7, listRoom.size());
		for (int i = 0; i < listRoom.size(); i++) {
			Assert.assertFalse(listRoom.get(i).getId() == 1);
			Assert.assertFalse(listRoom.get(i).getId() == 3);
			Assert.assertFalse(listRoom.get(i).getId() == 5);
		}
	}

	@Test
	public void testSearchFreeRoom4() {
		Date checkin = new GregorianCalendar(2020, Calendar.MAY, 2).getTime();
		Date checkout = new GregorianCalendar(2020, Calendar.MAY, 5).getTime();
		ArrayList<Staff> listRoom = rd.searchFreeRoom(checkin, checkout);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(8, listRoom.size());
		for (int i = 0; i < listRoom.size(); i++) {
			Assert.assertFalse(listRoom.get(i).getId() == 3);
			Assert.assertFalse(listRoom.get(i).getId() == 5);
		}
	}

	@Test
	public void testSearchFreeRoom5() {
		Date checkin = new GregorianCalendar(2020, Calendar.MAY, 9).getTime();
		Date checkout = new GregorianCalendar(2020, Calendar.MAY, 11).getTime();
		ArrayList<Staff> listRoom = rd.searchFreeRoom(checkin, checkout);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(9, listRoom.size());
		for (int i = 0; i < listRoom.size(); i++) {
			Assert.assertFalse(listRoom.get(i).getId() == 1);
		}
	}

	@Test
	public void testSearchFreeRoom6() {
		Date checkin = new GregorianCalendar(2020, Calendar.MAY, 11).getTime();
		Date checkout = new GregorianCalendar(2020, Calendar.MAY, 13).getTime();
		ArrayList<Staff> listRoom = rd.searchFreeRoom(checkin, checkout);
		Assert.assertNotNull(listRoom);
		Assert.assertEquals(10, listRoom.size());
	}
}
