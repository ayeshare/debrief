package edu.nps.moves.examples;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Vector3Double;

/**
 * Creates and sends ESPDUs in IEEE binary format.
 *
 * @author DMcG
 */
public class OpenHouseSender {
	/** multicast group we send on */
	public static final String MULTICAST_GROUP = "239.8.9.10";
	public static final String UNICAST_DESTINATION = "172.20.193.22";

	/** Port we send on */
	public static final int PORT = 62050;

	public static void main(final String args[]) {
		final EntityStatePdu espdu = new EntityStatePdu();
		MulticastSocket socket;
		InetAddress address;

		espdu.setExerciseID((short) 0);

		// The EID is the unique identifier for objects in the world. This
		// EID should match up with the ID for the object specified in the
		// VMRL/x3d world.
		final EntityID eid = espdu.getEntityID();
		eid.setSite((short) 1);
		eid.setApplication((short) 0); // 1
		eid.setEntity((short) 210); // 2

		try {
			socket = new MulticastSocket(PORT);
			address = InetAddress.getByName(MULTICAST_GROUP);
			final InetAddress unicastDestination = InetAddress.getByName(UNICAST_DESTINATION);
			socket.joinGroup(address);

			while (true) {
				for (int idx = 0; idx < 100; idx++) {
					// The timestamp should be monotonically increasing. Many implementations
					// discard packets that have earlier timestamps (assumption is that it
					// arrived out of order) or non-increasing timestamp (dupe packet).
					// The time should be slaved to clock time, so we can determine the time
					// between packets, but this is the minimum for testing.
					long timestamp = espdu.getTimestamp();
					timestamp++;
					espdu.setTimestamp(timestamp);

					// Modify the x-axis position of the object
					// 36.595517, -121.877939
					final Vector3Double location = espdu.getEntityLocation();
					location.setX(507.5);
					location.setY(391.5);
					location.setZ(-3.7);

					// Do some rotation to make sure that works
					final Orientation orientation = espdu.getEntityOrientation();
					// float psi = orientation.getPsi();
					// psi = psi + idx;
					// orientation.setPsi(psi);
					orientation.setTheta(3.7f);

					// Marshal out the object to a byte array, then send a datagram
					// packet with that data in it.
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					final DataOutputStream dos = new DataOutputStream(baos);
					espdu.marshal(dos);
					final byte[] data = baos.toByteArray();
					final DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);

					socket.send(packet);
					Thread.sleep(1000);

					System.out.print("Moving espdu EID=[" + eid.getSite() + "," + eid.getApplication() + ","
							+ eid.getEntity() + "]");
					System.out.println(
							" Location=[" + location.getX() + "," + location.getY() + "," + location.getZ() + "]");

				}
			}
		} catch (final Exception e) {
			System.out.println(e);
		}

	}

}
