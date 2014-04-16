package me.felix;

import java.io.PrintWriter;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SenseMe extends Activity implements SensorEventListener {
	TextView gyro_view, acce_view, ori_view, mag_view;
	TextView view;
	String viewText;

	SensorManager sensorManager;
	Sensor gyro_sensor, acce_sensor, ori_sensor, mag_sensor;
	String gyro_file, acce_file, ori_file, mag_file;
	PrintWriter gyro_writer, acce_writer, ori_writer, mag_writer;
	PrintWriter writer;

	long timestamp;
	float data_x, data_y, data_z;
	String data;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// init views
		gyro_view = (TextView) findViewById(R.id.gyro_view);
		acce_view = (TextView) findViewById(R.id.acce_view);
		ori_view = (TextView) findViewById(R.id.ori_view);
		mag_view = (TextView) findViewById(R.id.mag_view);
		// init sensors
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		gyro_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		acce_sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		ori_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		// setup filenames
		timestamp = System.currentTimeMillis();
		gyro_file = String.format("/sdcard/gyro_%d.txt", timestamp);
		acce_file = String.format("/sdcard/acce_%d.txt", timestamp);
		ori_file = String.format("/sdcard/ori_%d.txt", timestamp);
		mag_file = String.format("/sdcard/mag_%d.txt", timestamp);
		// open files for write
		try {
			gyro_writer = new PrintWriter(gyro_file);
			acce_writer = new PrintWriter(acce_file);
			ori_writer = new PrintWriter(ori_file);
			mag_writer = new PrintWriter(mag_file);

		} catch (Exception e) {
			Log.e("Felix", "Error opening file.");
			e.printStackTrace();
		}

	}

	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, gyro_sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, acce_sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, ori_sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, mag_sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		timestamp = event.timestamp;
		data_x = event.values[0];
		data_y = event.values[1];
		data_z = event.values[2];
		data = String
				.format("%f %f %f %d\n", data_x, data_y, data_z, timestamp);

		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_GYROSCOPE:
				// acceleration sensor
				view = gyro_view;
				viewText = String.format("Gyroscope: %s", data);
				writer = gyro_writer;
				break;

			case Sensor.TYPE_LINEAR_ACCELERATION:
				// acceleration sensor
				view = acce_view;
				viewText = String.format("Acceleration: %s", data);
				writer = acce_writer;
				break;

			case Sensor.TYPE_ORIENTATION:
				// acceleration sensor
				view = ori_view;
				viewText = String.format("Orientation: %s", data);
				writer = ori_writer;
				break;

			case Sensor.TYPE_MAGNETIC_FIELD:
				// acceleration sensor
				view = mag_view;
				viewText = String.format("Magnetic Field: %s", data);
				writer = mag_writer;
				break;

			default:
				break;
			}

			view.setText(viewText);
			try {
				writer.println(data);
				writer.flush();
			} catch (Exception e) {
				Log.e("Felix", "Error writing data.");
				e.printStackTrace();
			}
		}

	}
}