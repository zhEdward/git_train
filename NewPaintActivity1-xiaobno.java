package com.android.androidpaint;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cocos2dx.cpp.AppActivity;
import org.json.JSONArray;
import org.json.JSONException;

import com.android.androidpaint.enums.Colors;
import com.android.androidpaint.enums.Sizes;
import com.fjfxyy.zxb.fragment.AnimalParkFragment;
import com.fjfxyy.zxb.fragment.PlayerFragment;
import com.fjfxyy.zxb.fragment.ThemePlayFragment;
import com.fxyy.zxb.checkpoint.fragment.CheckpointFragment;
import com.lx.video.PlayerActivity;
import com.net.download.DownloadHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fxyy.linx.androidpaint.enums.Figures;
import fxyy.linx.checkpoint.CP_Constant;
import fxyy.linx.checkpoint.CheckpointActivity;
import fxyy.linx.checkpoint.bean.CPCallbackIntent;
import fxyy.lx.ktjx.AnimalParkActivity;
import fxyy.lx.ktjx.App;
import fxyy.lx.ktjx.CardActivityCompat;
import fxyy.lx.ktjx.ListeningPracticeActivity;
import fxyy.lx.ktjx.PhotoViewerActivity;
import fxyy.lx.ktjx.TeachingPlanActivity;
import fxyy.lx.ktjx.bean.Obj_Gendeng;
import fxyy.lx.ktjx.bean.Obj_Student;
import fxyy.lx.ktjx.db.DatabaseHelper;
import fxyy.lx.ktjx.dialog.CallbackBundle;
import fxyy.lx.ktjx.dialog.DialogAnswer;
import fxyy.lx.ktjx.dialog.DialogRandom;
import fxyy.lx.ktjx.dialog.DialogStartClassGV;
import fxyy.lx.ktjx.dialog.OpenBookDialog;
import fxyy.lx.ktjx.dialog.OpenFileDialog;
import fxyy.lx.ktjx.dialog.OpenKtjxFileDialog;
import fxyy.lx.ktjx.themes.Theme12Activity;
import fxyy.lx.ktjx.themes.Theme13Activity;
import fxyy.lx.ktjx.themes.Theme2Activity;
import fxyy.lx.ktjx.themes.Theme3Activity;
import fxyy.lx.ktjx.themes.Theme7Activity;
import fxyy.lx.ktjx.themesplay.TPlayActivity;
import fxyy.lx.ktjx.utils.InitBookUtil;
import fxyy.lx.ktjx.utils.MaterialDialog;
import fxyy.lx.ktjx.utils.MyGson;
import fxyy.lx.ktjx.utils.MyToast;
import fxyy.lx.ktjx.utils.NetService;
import fxyy.lx.ktjx.utils.ReadKetangUtil;
import fxyy.lx.ktjx.utils.TS;
import fxyy.lx.ktjx_guzheng_tv.R;
import fxyy.wjg.tongxun_juyuwang.SocketService;

public class NewPaintActivity1 extends FragmentActivity {

	public static final String LAST_COLOR = "pl.android.androidpaint.LAST_COLOR";

	public static final int PICK_COLOR_REQUEST = 1;

	private PaintView paintView;
	private Button button_color;
	private Button button_size;
	private Button button_pencil;
	private Button button_line;
	private Button button_circle;
	private Button button_rectangle;
	private Button button_eraser;
	private Button button_fill;
	private Button button_undo;
	private int lastColor;
	private int lastSize;

	private ImageView button_play;

	private TextView txv_bookname;

	private ThemePlayFragment tPFFragment;

	private PlayerFragment playerFragment;

	private AnimalParkFragment animalParkFragment;

	private CheckpointFragment checkpointFragment;

	private FragmentManager fragmentManager;

	public void doCircle(View view) {
		paintView.setFigure(Figures.CIRCLE);
		paintView.setColor(lastColor);
		paintView.setSize(lastSize);
		button_color.setEnabled(true);
		doFocus(button_circle);
	}

	public void doClear(View view) {
		paintView.undo(Integer.MAX_VALUE);
		updateUndoButton();

		initImage(bookname);
		// TODO openIMG(App.ketangPath + App.temp_bookname + "/book/" +
		// bookname);

	}

	// public void doColor(View view) {
	// Intent intent = new Intent(this, ColorActivity.class);
	// intent.putExtra(LAST_COLOR, lastColor);
	// startActivityForResult(intent, PICK_COLOR_REQUEST);
	// }

	public void doEraser(View view) {
		paintView.setFigure(Figures.POINT);
		paintView.setColor(Color.WHITE);
		paintView.setSize(lastSize);
		button_color.setEnabled(false);

		doFocus(button_eraser);
	}

	public void doFill(View view) {

		String[] items = new String[] { getResources().getText(R.string.high_tolerance).toString(),
				getResources().getText(R.string.low_tolerance).toString() };

		ListView listView = initListView(items);
		final MaterialDialog alert = new MaterialDialog(this).setTitle(getResources().getText(R.string.fill))
				.setContentView(listView).setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();

				switch (arg2) {
				case 0:
					paintView.setTolerance(128);
					button_fill.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fill_high, 0, 0);
					break;
				case 1:
					paintView.setTolerance(0);
					button_fill.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fill_low, 0, 0);
					break;

				}

				paintView.setFigure(Figures.FILL);
				paintView.setColor(lastColor);
				paintView.setSize(lastSize);
				button_color.setEnabled(true);

				doFocus(button_fill);
			}
		});
	}

	private void doFocus(Button button) {
		button_pencil.setTypeface(null, Typeface.NORMAL);
		button_line.setTypeface(null, Typeface.NORMAL);
		button_circle.setTypeface(null, Typeface.NORMAL);
		button_rectangle.setTypeface(null, Typeface.NORMAL);
		button_eraser.setTypeface(null, Typeface.NORMAL);
		button_fill.setTypeface(null, Typeface.NORMAL);

		button.setTypeface(null, Typeface.BOLD);
	}

	public void doLine(View view) {
		paintView.setFigure(Figures.LINE);
		paintView.setColor(lastColor);
		paintView.setSize(lastSize);
		button_color.setEnabled(true);

		doFocus(button_line);
	}

	/**
	 * 打开图片
	 */
	static private int openfileDialogId = 0;
	/**
	 * 打开mp3
	 */
	static private int openfileDialogIdmp3 = 1;

	public void doOpen(View view) {
		showDialog(openfileDialogId);
	}

	public void doChooice(View view) {
		showDialog(openfileDialogIdmp3);
	}

	private String bookname = "";

	public void doLast(View view) {

		if (!bookname.equals(App.BookMuluList.get(0))) {
			for (int i = 0; i < App.BookMuluList.size(); i++) {
				if (App.BookMuluList.get(i).equals(bookname) && i <= App.BookMuluList.size() - 1 && i > 0) {
					bookname = App.BookMuluList.get(i - 1);
					initImage(bookname);
					// TODO openIMG(App.ketangPath + App.temp_bookname +
					// "/book/" +
					// bookname);
					break;
				}
			}
		} else {
			MyToast.toastShow("已是第一章了哦！");
			return;
		}
		MyToast.Cancel();
	}

	private int flowPosition;

	public void doNext(View view) {

		if (new File(App.ketangPath + App.temp_bookname + "/" + "ketanglc.en").exists()) {
			doReadConfigurationFile("ketanglc.en", "流程", false, flowPosition);
			flowPosition++;
		}
		// flowPosition++;
		ArrayList<String> bookMuluLists = App.BookMuluList;

		if (!bookname.equals(bookMuluLists.get(bookMuluLists.size() - 1))) {
			for (int i = 0; i < bookMuluLists.size(); i++) {
				if (bookMuluLists.get(i).equals(bookname) && i >= 0 && i < bookMuluLists.size() - 1) {
					bookname = bookMuluLists.get(i + 1);
					initImage(bookname);
					// TODO openIMG(App.ketangPath + App.temp_bookname +
					// "/book/" +
					// bookname);
					break;
				}
			}
		} else {
			MyToast.toastShow("已是最后一章了哦！");
			return;
		}
		MyToast.Cancel();
	}

	ImageLoader mLoader = ImageLoader.getInstance();

	public void openIMG(String path) {
		try {
			BitmapFactory.Options bfOptions = new BitmapFactory.Options();
			bfOptions.inDither = false;// 使图片不抖动。不是很懂
			bfOptions.inPurgeable = true;// 使得内存可以被回收
			bfOptions.inTempStorage = new byte[12 * 1024]; // 临时存储
			bfOptions.inInputShareable = true;
			bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			bfOptions.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeFile(path, bfOptions);

			System.out.println("??:" + path);
			if (bitmap == null) {
				imgHandler.sendEmptyMessage(-1);
				TS.show("没有找到课本,请手动选择");
				throw new Exception(getResources().getText(R.string.error_opening_image) + "\n" + path);
			}

			bitmap = Bitmap.createScaledBitmap(bitmap, paintView.getWidth(), paintView.getHeight(), true);
			mOpenBitmap = bitmap;

			imgHandler.sendEmptyMessage(0);// 大图片加载完毕 异步通知

			// paintView.undo(Integer.MAX_VALUE);
			// updateUndoButton();
			// paintView.open(bitmap);

			String[] name = path.split("/");
			bookname = name[name.length - 1];
			bitmap = null;
			System.gc();
			System.out.println(getResources().getText(R.string.message_opening_image) + "\n" + path);
		} catch (Exception e) {
			Log.e(TAG, "???:" + e.getMessage());
			imgHandler.sendEmptyMessage(-1);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
			imgHandler.sendEmptyMessage(-1);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == openfileDialogId) {
			Map<String, Integer> images = new HashMap<String, Integer>();
			// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
			images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
			images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // 返回上一层的图标
			images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
			images.put("jpg", R.drawable.ic_img); // wav文件图标
			images.put("png", R.drawable.ic_img);
			images.put(OpenFileDialog.sEmpty, R.drawable.ic_logo);
			Dialog dialog = null;
			if (App.huaban) {
				dialog = OpenKtjxFileDialog.createDialog(id, this, "打开课本", new CallbackBundle() {
					@Override
					public void callback(Bundle bundle) {
						String filepath = bundle.getString("path");
						try {
							String path = filepath;

							BitmapFactory.Options bfOptions = new BitmapFactory.Options();
							bfOptions.inDither = false;// 使图片不抖动。不是很懂
							bfOptions.inPurgeable = true;// 使得内存可以被回收
							bfOptions.inTempStorage = new byte[12 * 1024]; // 临时存储
							bfOptions.inInputShareable = true;
							bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;

							Bitmap bitmap = BitmapFactory.decodeFile(path, bfOptions);
							// Bitmap bitmap = BitmapFactory.decodeFile(path);
							if (bitmap == null) {
								TS.show("没有找到图片,请手动选择");
								throw new Exception(getResources().getText(R.string.error_opening_image) + "\n" + path);

							}
							bitmap = Bitmap.createScaledBitmap(bitmap, paintView.getWidth(), paintView.getHeight(),
									true);
							paintView.undo(Integer.MAX_VALUE);
							updateUndoButton();
							paintView.open(bitmap);
							System.out.println(getResources().getText(R.string.message_opening_image) + "\n" + path);
							String[] name = path.split("/");
							bookname = name[name.length - 1];
							bitmap = null;
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}

					}
				}, ".jpg;.png;", images, App.ketangPath + App.temp_bookname + "/book/");
			} else {
				dialog = OpenKtjxFileDialog.createDialog(id, this, "打开主题", new CallbackBundle() {

					@Override
					public void callback(Bundle bundle) {
						String filepath = bundle.getString("path");
						try {
							String path = filepath;
							// Bitmap bitmap = BitmapFactory.decodeFile(path);
							BitmapFactory.Options bfOptions = new BitmapFactory.Options();
							bfOptions.inDither = false;// 使图片不抖动。不是很懂
							bfOptions.inPurgeable = true;// 使得内存可以被回收
							bfOptions.inTempStorage = new byte[12 * 1024]; // 临时存储
							bfOptions.inInputShareable = true;
							bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;

							Bitmap bitmap = BitmapFactory.decodeFile(path, bfOptions);
							if (bitmap == null) {
								TS.show("没有找到图片,请手动选择");
								throw new Exception(getResources().getText(R.string.error_opening_image) + "\n" + path);

							}
							bitmap = Bitmap.createScaledBitmap(bitmap, paintView.getWidth(), paintView.getHeight(),
									true);
							paintView.undo(Integer.MAX_VALUE);
							updateUndoButton();
							paintView.open(bitmap);
							System.out.println(getResources().getText(R.string.message_opening_image) + "\n" + path);
							String[] name = path.split("/");
							bookname = name[name.length - 1];
							bitmap = null;
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}, ".jpg;.png;", images, App.themePath);
			}

			return dialog;
		}
		if (id == openfileDialogIdmp3) {

			Map<String, Integer> images = new HashMap<String, Integer>();
			// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
			images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
			images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // 返回上一层的图标
			images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
			images.put("mp3", R.drawable.ic_music2); // wav文件图标
			images.put(OpenFileDialog.sEmpty, R.drawable.ic_logo);
			Dialog dialog = OpenKtjxFileDialog.createDialog(id, this, "打开音乐", new CallbackBundle() {
				@Override
				public void callback(Bundle bundle) {
					String filepath = bundle.getString("path");
					try {
						if (mediaPlayer != null) {
							mediaPlayer.stop();
							mediaPlayer.reset();
						}
						mediaPlayer = new MediaPlayer();
						mediaPlayer.setDataSource(filepath);
						mediaPlayer.prepare();
						mediaPlayer.start();
						currentS = 0;
						App.tempMusicPath = filepath;
						// 图标状态修改
						button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_stop));
						// button_play.setText("暂停");
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer arg0) {
								// 图标状态修改
								button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
								// button_play.setText("播放");
							}
						});
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				}
			}, ".mp3;", images, App.musicPath);
			return dialog;
		}

		return null;
	}

	public void doOpenBook(View view) {
		Map<String, Integer> images = new HashMap<String, Integer>();
		// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
		images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
		images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // 返回上一层的图标
		images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
		images.put("jpg", R.drawable.ic_img); // wav文件图标
		images.put("png", R.drawable.ic_img);
		images.put(OpenFileDialog.sEmpty, R.drawable.ic_logo);
		MaterialDialog md = OpenBookDialog.createDialog(0, this, "打开课本", new CallbackBundle() {
			@Override
			public void callback(Bundle bundle) {
				String filepath = bundle.getString("path");
				try {
					String path = filepath;
					BitmapFactory.Options bfOptions = new BitmapFactory.Options();
					bfOptions.inDither = false;// 使图片不抖动。不是很懂
					bfOptions.inPurgeable = true;// 使得内存可以被回收
					bfOptions.inTempStorage = new byte[12 * 1024]; // 临时存储
					bfOptions.inInputShareable = true;
					bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
					Bitmap bitmap = BitmapFactory.decodeFile(path, bfOptions);

					if (bitmap == null) {
						TS.show("没有找到图片,请手动选择");
						throw new Exception(getResources().getText(R.string.error_opening_image) + "\n" + path);
					}
					bitmap = Bitmap.createScaledBitmap(bitmap, paintView.getWidth(), paintView.getHeight(), true);
					paintView.undo(Integer.MAX_VALUE);
					updateUndoButton();
					paintView.open(bitmap);
					System.out.println(getResources().getText(R.string.message_opening_image) + "\n" + path);
					String[] name = path.split("/");
					bookname = name[name.length - 1];
					bitmap = null;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}, ".jpg;.png;", images, App.ketangPath + App.temp_bookname + "/book/");
		md.show();
	}

	public void doPencil(View view) {
		paintView.setFigure(Figures.POINT);
		paintView.setColor(lastColor);
		paintView.setSize(lastSize);
		button_color.setEnabled(true);
		doFocus(button_pencil);
	}

	public void doRectangle(View view) {
		paintView.setFigure(Figures.RECTANGLE);
		paintView.setColor(lastColor);
		paintView.setSize(lastSize);
		button_color.setEnabled(true);
		doFocus(button_rectangle);
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'tmp'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public void doSave(View view) {
		Bitmap bitmap = Bitmap.createBitmap(paintView.getWidth(), paintView.getHeight(), Bitmap.Config.ARGB_8888);
		paintView.draw(new Canvas(bitmap));
		String path = App.notePath + getPhotoFileName();
		File file = new File(path);
		try {
			file.createNewFile();
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			if (getResources().getText(R.string.screenshot_extension).toString().toUpperCase(Locale.US)
					.endsWith("JPG")) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} else {
				throw new IOException((getResources().getText(R.string.error_saving_image)) + "\n" + path);
			}
			TS.show("保存成功!位置:" + path);
			System.out.println(getResources().getText(R.string.message_saving_image) + "\n" + path);
		} catch (IOException e) {
			System.out.println(e.getMessage() + "\n" + path);
			TS.show("保存失败!");
		}
	}

	public void doSize(View view) {
		String[] item = new String[Sizes.values().length];
		for (int i = 0; i < item.length; i++) {
			item[i] = Sizes.values()[i].toString();
		}
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("尺寸").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();
				lastSize = Sizes.values()[arg2].getSize();
				paintView.setSize(lastSize);
			}
		});
	}

	public void doColor(View view) {

		String[] item = getResources().getStringArray(R.array.colors_array);
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("颜色").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				String color = "Red";

				switch (arg2) {
				case 0:
					color = "Red";
					break;
				case 1:
					color = "Green";
					break;
				case 2:
					color = "Blue";
					break;
				case 3:
					color = "Cyan";
					break;
				case 4:
					color = "Magenta";
					break;
				case 5:
					color = "Yellow";
					break;
				case 6:
					color = "Black";
					break;

				}
				lastColor = Color.parseColor(color);
				paintView.setColor(lastColor);
				button_color.setTextColor(lastColor);
				alert.dismiss();
			}
		});
	}

	boolean mediaStop = true;

	public void doMore(View view) {
		String[] item = new String[] { "选人", "锁屏", "解屏", "自由练习" };
		// , "静音", "复位" };
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("操作").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();
				// mediaStop = false;
				Intent intent;
				Bundle bundle;
				switch (arg2) {
				// case 0:
				// intent = new Intent(NewPaintActivity1.this,
				// DialogStudentChooice.class);
				// bundle = new Bundle();
				// bundle.putInt("type", 3);
				// intent.putExtras(bundle);
				// startActivity(intent);
				// break;
				// case 1:
				// intent = new Intent(NewPaintActivity1.this,
				// DialogStudentChooice.class);
				// bundle = new Bundle();
				// bundle.putInt("type", 2);
				// intent.putExtras(bundle);
				// startActivity(intent);
				// break;
				// case 2:
				// intent = new Intent(NewPaintActivity1.this,
				// DialogStudentChooice.class);
				// bundle = new Bundle();
				// bundle.putInt("type", 4);
				// intent.putExtras(bundle);
				// startActivity(intent);
				// break;
				// case 3:
				// intent = new Intent(NewPaintActivity1.this,
				// DialogStudentChooice.class);
				// bundle = new Bundle();
				// bundle.putInt("type", 1);
				// intent.putExtras(bundle);
				// startActivity(intent);
				// break;
				// case 4:
				// intent = new Intent(NewPaintActivity1.this,
				// DialogRandom.class);
				// startActivity(intent);
				// break;
				// case 5:
				// SocketService.SendMSG("-heiping");
				// break;
				// case 6:
				// SocketService.SendMSG("-baiping");
				// break;
				// case 7:
				// SocketService.SendMSG("-ziyou");
				// break;

				case 0:
					intent = new Intent(NewPaintActivity1.this, DialogRandom.class);
					startActivity(intent);
					break;
				case 1:
					SocketService.SendMSG("-heiping");
					break;
				case 2:
					SocketService.SendMSG("-baiping");
					break;
				case 3:
					SocketService.SendMSG("-ziyou");
					break;
				// case 4:
				// SocketService.SendMSG("-jingying");
				// break;
				// case 5:
				// SocketService.SendMSG("-fuwei");
				// break;
				}
			}
		});

	}

	public void doUse(View view) {

		// String[] item = new String[] { "直线", "圆形", "矩形", "消除", "填充" };
		String[] item = new String[] { "直线", "圆形", "矩形", "填充", "尺寸", "颜色" };
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("常用工具").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();
				switch (arg2) {
				case 0:
					doLine(null);
					break;
				case 1:
					doCircle(null);
					break;
				case 2:
					doRectangle(null);
					break;
				case 3:
					doFill(null);
					break;
				case 4:
					doSize(null);
					break;
				case 5:
					doColor(null);
					break;
				}
			}
		});
	}

	public void doOperation(View view) {
		doReadConfigurationFile("ketang.en", "操作", true, 0);
	}

	/** 记录所选择的曲目 **/
	String qumu = "";

	/**
	 * 根据具体文件名 从底层文件 读取数据
	 * 
	 * @param file
	 *            读取底层配置文件的名称
	 * @param mTitle
	 *            弹出对话框中显示的标题
	 * @param isNeedLsv
	 *            是否需要显示对话框
	 * @param ptInFile
	 *            在底层配置文件中的第几行 如果要显示对话框，则设置为零,索引从1开始。
	 */
	private void doReadConfigurationFile(String file, String mTitle, boolean isNeedLsv, int ptInFile) {
		/**
		 * 文件夹路径
		 */
		final String path = App.ketangPath + App.temp_bookname + "/";
		final List<String[]> list = ReadKetangUtil.ketang(path + file);

		if (list.size() > 0) {
			String[] items = new String[list.size()];
			for (int i = 0; i < items.length; i++) {
				int type = Integer.parseInt(list.get(i)[0]);

				// 1:画图 huatu
				// 2:曲谱练习 qupulianxi
				// 3:大气球 daqiqiu
				// 4:敲砖块 qiaozhuankuai
				// 5:曲谱考试 qupukaoshi
				// 6:气球大挑战 qiqiudatiaozhan
				// 7:动画 donghua
				// 8:音乐 yinyue
				// 9:视频 shiping
				// 10:卡片 kapian
				String title = "其他";
				switch (type) {
				case 1:
					title = "画图";
					break;
				case 2:
					title = "曲谱练习";
					break;
				case 3:
					title = "打气球";
					break;
				case 4:
					title = "敲砖块";
					break;
				case 5:
					title = "曲谱考试";
					break;
				case 6:
					title = "气球大挑战";
					break;
				case 7:
					title = "动画";
					break;
				case 8:
					title = "音乐";
					break;
				case 9:
					title = "视频";
					break;
				case 10:
					title = "卡片";
					break;
				case 11:
					title = "曲目";
					break;
				case 12:
					title = "引题";
					break;
				case 13:
					title = "知识点";
					break;
				case 14:
					title = "活动";
					break;
				case 15:
					title = "关卡";
					break;
				case 16:
					title = "关卡";
					break;
				case 20:
					title = "游戏";
					break;
				}
				items[i] = new String(title + "-" + list.get(i)[1]);
			}
			if (isNeedLsv) {
				showDialogCtainLsv(mTitle, path, list, items);
			} else {
				if (ptInFile >= 0) {

					if (ptInFile <= list.size() - 1) {
						doDetailOperation(path, list, ptInFile, isNeedLsv);
					}

				} else {
					TS.show("请检查配置文件");
				}
			}
		}

	}

	/**
	 * 显示包含ListView的对话框
	 * 
	 * @param mTitle
	 *            对话框标题
	 * @param path
	 *            文件夹路径
	 * @param list
	 *            配置文件列表
	 * @param items
	 *            操作类型和名称
	 */
	private void showDialogCtainLsv(String mTitle, final String path, final List<String[]> list, String[] items) {

		ListView listView = initListView(items);

		final MaterialDialog alert = new MaterialDialog(this).setTitle(mTitle).setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));

		// View inflater =
		// LayoutInflater.from(this).inflate(R.layout.layout_materialdialog2,
		// null);
		//
		// final AlertDialog alert = new
		// AlertDialog.Builder(this).setView(listView).create();

		alert.setCanceledOnTouchOutside(true);

		alert.show();

		doFocuse(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				alert.dismiss();

				doDetailOperation(path, list, arg2, true);

			}

		});
	}

	private void RunApp(String packageName) {
		// PackageInfo pi;
		// try {
		// pi = getPackageManager().getPackageInfo(packageName, 0);
		//
		// Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		//
		// resolveIntent.setPackage(pi.packageName);
		//
		// PackageManager pManager = getPackageManager();
		//
		// List apps = pManager.queryIntentActivities(resolveIntent, 0);
		//
		// ResolveInfo ri = (ResolveInfo) apps.iterator().next();

		// if (ri != null) {

		// packageName = ri.activityInfo.packageName;
		//
		// String className = ri.activityInfo.name;
		//
		Intent intent = new Intent(this, AppActivity.class);

		// ComponentName cn = new ComponentName(packageName, className);

		// intent.setComponent(cn);

		String path = App.ketangPath + App.temp_bookname;

		intent.putExtra("CP_Path", path);

		intent.putExtra("GameType", "basketball");

		String tempBook = "";

		if ("".equals(App.book)) {
			tempBook = "1";
		}

		intent.putExtra("Title", "第" + tempBook + "课" + " " + App.temp_bookname);
		startActivity(intent);
		// }
		// handler.sendEmptyMessageDelayed(2, 10000);
		// } catch (NameNotFoundException e) {
		//
		// e.printStackTrace();
		//
		// }

	}

	public void startGame(String packageName) {

		String ktString = ReadKetangUtil.getJson(App.ketangPath + App.temp_bookname + "/cp/cp.en");

		if (!"".equals(ktString)) {

			if (ReadKetangUtil.writeFile_txt_utf8(App.ketangPath + App.temp_bookname + "/cp/cp3.en", ktString)) {

				RunApp(packageName);

			}

		} else {

			return;

		}

	}

	private void startPhotoViewerActivity(int type) {
		Intent intent = new Intent(NewPaintActivity1.this, PhotoViewerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void doQumu() {

		// String[] item = new String[] { "直线", "圆形", "矩形", "消除", "填充" };
		// String[] item = new String[] { "示范", "有提示弹奏", "无提示弹奏", "考试", "技术讲解",
		// "演奏背景" };
		// String[] item = new String[] { "示范", "跟弹" };
		String[] item = new String[] { "跟弹" };
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("曲目").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();
				switch (arg2) {
				// case 0:
				// PlayShifanVideo();
				// break;
				case 0:
					startThemesPlayActivity();
					break;
				case 2:

					break;
				case 3:

					break;
				case 4:

					break;
				case 5:

					break;
				}
			}
		});
	}

	Obj_Gendeng obj_Gendeng;

	private void initConfig(int qu) {
		String json = ReadKetangUtil.getJson(App.ketangPath + App.temp_bookname + "/gd/config.en");
		try {
			if (json.length() > 0) {
				obj_Gendeng = new Obj_Gendeng();
				JSONArray ja = new JSONArray(json);
				for (int i = 0; i < ja.length(); i++) {
					Obj_Gendeng o = (Obj_Gendeng) MyGson.JsonToObj(ja.get(i).toString(), new Obj_Gendeng());
					if (o.getIndex().equals(qu + "")) {
						obj_Gendeng = o;
						break;
					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void PlayShifanVideo() {
		if (!App.temp_bookname.equals("未选择")) {
			// 曲
			int cqm = 0;
			String[] qm = qumu.split("_");
			if (qm.length > 1) {
				cqm = Integer.parseInt(qumu.split("_")[0]);
				initConfig(cqm);

				Intent intent = new Intent(NewPaintActivity1.this, PlayerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("videopath", App.ketangPath + App.temp_bookname + "/" + obj_Gendeng.getVideo());
				intent.putExtras(bundle);
				startActivity(intent);
			}

		}
	}

	/**
	 * 跳转到跟灯闪的页面
	 */
	private void startThemesPlayActivity() {
		if (!App.temp_bookname.equals("未选择")) {
			int cqm = 0;
			String[] qm = qumu.split("_");
			if (qm.length > 1) {
				cqm = Integer.parseInt(qumu.split("_")[0]);
			}

			int i = 1;
			String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(App.temp_bookname);
			i = Integer.parseInt(m.replaceAll("").trim());
			Intent intent = null;
			Bundle bundle = null;

			intent = new Intent(NewPaintActivity1.this, TPlayActivity.class);
			bundle = new Bundle();
			bundle.putInt("jie", i);
			bundle.putInt("qu", cqm);
			intent.putExtras(bundle);
			if (intent != null) {
				startActivity(intent);
			}
		}
	}

	public String getstave(String str1) {
		String str = "";
		StringBuffer buffer = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(str1);
			try {
				InputStreamReader isr = new InputStreamReader(fis, "GBK");
				Reader in = new BufferedReader(isr);
				int ch;
				try {
					while ((ch = in.read()) > -1) {

						buffer.append((char) ch);
						// System.out.println("{-"+buffer.toString()+"-}");
					}
					in.close();
					str = buffer.toString();
					fis.close();
					isr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str;
	}

	public void doUndo(View view) {
		paintView.undo(1);
		updateUndoButton();
	}

	public void doExit(View view) {
		finish();
		// System.exit(0);
	}

	private MediaPlayer mediaPlayer = null;

	public void doPlay(View view) {
		if (mediaPlayer != null && !App.tempMusicPath.equals("")) {
			try {
				if (mediaPlayer.isPlaying()) {
					// 暂停
					mediaPlayer.pause();
					button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
					// button_play.setText("播放");
				} else {

					mediaPlayer.start();
					// currentS = 0;
					button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_stop));
					// button_play.setText("暂停");
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer arg0) {
							// 图标状态修改
							button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
							// button_play.setText("播放");
						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
				button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
				// button_play.setText("播放");
			}

		} else {

			if (!App.tempMusicPath.equals("")) {

				try {
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setDataSource(App.tempMusicPath);
					mediaPlayer.prepare();
					mediaPlayer.start();
					currentS = 0;
					button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_stop));
					// button_play.setText("暂停");
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer arg0) {
							// 图标状态修改
							button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
							// button_play.setText("播放");
						}
					});
				} catch (

				IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 图标状态修改

			} else {

				if (mediaPlayer != null && mediaPlayer.isPlaying()) {

					mediaPlayer.stop();
					mediaPlayer.reset();
					App.tempMusicPath = "";
					button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));

				} else {
					MyToast.toastShow("请先选择一个音乐文件");
				}

			}

		}
	}

	/**
	 * 问候
	 * 
	 * @param view
	 */
	public void doPlayGreetings(View view) {
		doGreetings(1);
	}

	/**
	 * 再见
	 * 
	 * @param view
	 */
	public void doPlayGoodbye(View view) {

		doGreetings(2);
	}

	int currentS = 0;

	/**
	 * 问候
	 * 
	 * @param view
	 */
	public void doGreetings(int i) {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			mediaPlayer.stop();
			mediaPlayer.reset();
			App.tempMusicPath = "";
			if (i == currentS) {
				button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
				currentS = 0;
				return;
			}
		}

		try {
			currentS = i;
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.reset();
			}
			mediaPlayer = new MediaPlayer();
			if (i == 1 || i == 2) {

				if (i == 1) {
					mediaPlayer.setDataSource(App.ketangPath + "第1节" + "/上课歌合唱.mp3");
				}
				if (i == 2) {
					mediaPlayer.setDataSource(App.ketangPath + "第1节" + "/1_A0再见歌.mp3");
				}

				button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_stop));
			}
			mediaPlayer.prepare();
			mediaPlayer.start();

			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
					// 图标状态修改
					button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
					// button_play.setText("播放");
				}
			});

			// button_play.setText("暂停");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			TS.show("问候歌不存在");
		}
	}

	/**
	 * 教学
	 * 
	 * @param view
	 */
	public void doJiaoxue(View view) {
		doReadConfigurationFile("ketangjx.en", "主题教学", true, 0);
	}

	/**
	 * 弹奏
	 * 
	 * @param view
	 */
	public void doTanzou(View view) {
		doReadConfigurationFile("ketangtz.en", "主题弹奏", true, 0);
	}

	/**
	 * 主题创作
	 * 
	 * @param view
	 */
	public void doChuangzuo(View view) {

		App.list_answer.removeAll(App.list_answer);

		if (!App.temp_bookname.equals("未选择")) {
			int i = 1;
			String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(App.temp_bookname);
			i = Integer.parseInt(m.replaceAll("").trim());
			if (App.book.equals("2")) {
				TS.show("暂时没有该内容");
				return;
			}
			Intent intent = null;
			switch (i) {
			case 1:
				// intent = new Intent(NewPaintActivity1.this,
				// NewTheme1Activity.class);
				break;
			case 2:
				intent = new Intent(NewPaintActivity1.this, Theme2Activity.class);

				break;
			case 3:
				intent = new Intent(NewPaintActivity1.this, Theme3Activity.class);

				break;
			case 4:

				break;
			case 5:

				break;
			case 6:

				break;
			case 7:
				intent = new Intent(NewPaintActivity1.this, Theme7Activity.class);
				break;
			case 8:

				break;
			case 9:

				break;
			case 10:

				break;
			case 11:

				break;
			case 12:
				intent = new Intent(NewPaintActivity1.this, Theme12Activity.class);
				break;
			case 13:

				break;
			case 14:

				break;
			case 15:

				break;
			case 16:

				break;

			}
			if (intent != null) {
				startActivity(intent);
			} else {
				TS.show("这节课没有主题创作哦!");
			}
		}
	}

	/**
	 * 课后练习
	 * 
	 * @param view
	 */
	public void doKehouLX(View view) {
		String[] item = new String[] { "课后练习" };
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("课后练习").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();
				Intent intent;
				switch (arg2) {
				case 0:
					TS.show("TV版 还未兼容 暂未开放！");
					// try {
					// intent = new Intent(Intent.ACTION_MAIN);
					// intent.addCategory(Intent.CATEGORY_LAUNCHER);
					// ComponentName cn = new
					// ComponentName("material.fjfxyy.wsl.Activity",
					// "material.fjfxyy.wsl.Activity.MainActivity");
					// intent.setComponent(cn);
					// startActivity(intent);
					// } catch (Exception e) {
					// TS.show("请先安装课后练习app,再使用这个功能!");
					// }
					break;

				case 1:
					intent = new Intent(NewPaintActivity1.this, ListeningPracticeActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
	}

	public void doExample(View view) {
		// String[] item = new String[] { "教案", "视频" };
		String[] item = new String[] { "教案" };
		ListView listView = initListView(item);
		final MaterialDialog alert = new MaterialDialog(this).setTitle("示范").setContentView(listView)
				.setBackground(getResources().getDrawable(R.drawable.bg_addclass));
		alert.setCanceledOnTouchOutside(true);
		alert.show();
		doFocuse(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				alert.dismiss();
				Intent intent;
				Bundle bundle;
				switch (arg2) {
				case 0:
					// App.isCheck2 = false;
					// intent = new Intent(NewPaintActivity1.this,
					// DialogStartClassGV.class);
					// bundle = new Bundle();
					// bundle.putInt("type", 5);
					// intent.putExtras(bundle);
					// startActivityForResult(intent, 1000);
					intent = new Intent(NewPaintActivity1.this, TeachingPlanActivity.class);
					startActivity(intent);
					break;

				case 1:
					App.isCheck2 = false;
					intent = new Intent(NewPaintActivity1.this, DialogStartClassGV.class);
					bundle = new Bundle();
					bundle.putInt("type", 6);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1000);

					// intent = new Intent(NewPaintActivity1.this,
					// ListeningPracticeActivity.class);
					// startActivity(intent);
					break;
				}
			}
		});
	}

	/**
	 * 选择音乐
	 * 
	 * @param view
	 */
	public void doChooseMusic(View view) {
		doReadConfigurationFile("ketangyy.en", "音乐", true, 0);
	}

	private void initView() {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		// unregisterReceiver(receiver);
		MyToast.Cancel();
	}

	private void openImage(String filepath) {
		try {
			String path = filepath;
			BitmapFactory.Options bfOptions = new BitmapFactory.Options();
			bfOptions.inDither = false;// 使图片不抖动。不是很懂
			bfOptions.inPurgeable = true;// 使得内存可以被回收
			bfOptions.inTempStorage = new byte[12 * 1024]; // 临时存储
			bfOptions.inInputShareable = true;
			bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			bfOptions.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeFile(path, bfOptions);

			if (bitmap == null) {
				TS.show("没有找到图片,请手动选择");
				throw new Exception(getResources().getText(R.string.error_opening_image) + "\n" + path);

			}

			bitmap = Bitmap.createScaledBitmap(bitmap, paintView.getWidth(), paintView.getHeight(), true);

			paintView.undo(Integer.MAX_VALUE);
			updateUndoButton();

			paintView.open(bitmap);

			System.out.println(getResources().getText(R.string.message_opening_image) + "\n" + path);
			// bitmap.recycle();
			bitmap = null;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private ListView initListView(String[] item) {

		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.lv_item);

		ListView listView = new ListView(this);

		for (int i = 0; i < item.length; i++) {
			arrayAdapter.add(item[i]);
		}

		listView.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		listView.setFadingEdgeLength(0);
		listView.setCacheColorHint(0);
		listView.setDivider(getResources().getDrawable(R.drawable.ic_heng_xu));
		// listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

		float scale = getResources().getDisplayMetrics().density;
		int dpAsPixels = (int) (8 * scale + 0.5f);
		listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
		listView.setAdapter(arrayAdapter);
		return listView;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_COLOR_REQUEST) {
			if (resultCode == RESULT_OK) {
				lastColor = Color.parseColor(data.getStringExtra("kolor"));
				paintView.setColor(lastColor);
				button_color.setTextColor(lastColor);
			}
		}
		if (requestCode == 10001 && resultCode == 1000) {
			isFirstT = true;
			isFirst = true;
			initBook();

			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				App.tempMusicPath = "";
				button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
			}

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// if (mediaPlayer != null && mediaPlayer.isPlaying() && mediaStop) {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
		}
		mediaStop = true;
		MyToast.Cancel();
	}

	private DownloadHelper dh;
	private ImageView img_example;
	ActivityManager activityManger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_paint1);

		activityManger = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

		paintView = (PaintView) findViewById(R.id.PaintView);
		button_color = (Button) findViewById(R.id.button_color);
		button_size = (Button) findViewById(R.id.button_size);
		button_pencil = (Button) findViewById(R.id.button_pencil);
		button_line = (Button) findViewById(R.id.button_line);
		button_circle = (Button) findViewById(R.id.button_circle);
		button_rectangle = (Button) findViewById(R.id.button_rectangle);
		button_eraser = (Button) findViewById(R.id.button_eraser);
		button_fill = (Button) findViewById(R.id.button_fill);
		button_undo = (Button) findViewById(R.id.button_undo);

		button_play = (ImageView) findViewById(R.id.button_musicplay);
		img_example = (ImageView) findViewById(R.id.img_example);

		updateUndoButton();
		paintView.setButtonUndo(button_undo);

		paintView.setTolerance(0);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			button_fill.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fill_low, 0, 0);
		} else {
			button_fill.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fill_low, 0, 0);
		}

		lastColor = Colors.RED.getColor();
		lastSize = Sizes.MEDIUM.getSize();

		button_color.setTextColor(lastColor);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			button_size.setCompoundDrawablesWithIntrinsicBounds(0, Sizes.MEDIUM.getIcon(), 0, 0);
		} else {
			button_size.setCompoundDrawablesWithIntrinsicBounds(0, Sizes.MEDIUM.getIcon(), 0, 0);
		}

		doPencil(paintView);

		if (!App.huaban) {
			findViewById(R.id.button_last).setVisibility(View.GONE);
			findViewById(R.id.button_next).setVisibility(View.GONE);
		}
		doQueryStudent();
		initView();
		txv_bookname = (TextView) findViewById(R.id.txv_bookname);
		dh = new DownloadHelper(NewPaintActivity1.this);

		initBook();

		App.listac.add(this);
		// // 20160601 张小波 接受广播
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(CheckpointActivity.LOAD_CP);
		// filter.addAction(CheckpointActivity.CLOSE_CP);
		// filter.addAction(CheckpointActivity.OPEN_ANSWER);
		// filter.addAction(CheckpointActivity.OPEN_GD);
		// registerReceiver(receiver, filter);
		fragmentManager = getFragmentManager();
		// setTabSelection(0);

	}

	// private void addFragmentToStack(android.support.v4.app.Fragment fragment)
	// {
	// android.support.v4.app.FragmentTransaction ft =
	// getSupportFragmentManager().beginTransaction();
	// ft.replace(R.id.fgShowArea, fragment);
	// ft.commit();
	// }

	/**
	 * @param index
	 * @param data
	 *            向fragment传递的数据
	 */
	private void setTabSelection(int index, String data) {

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// hideFragments();
		switch (index) {
		case -1: //
			if (animalParkFragment == null) {
				animalParkFragment = new AnimalParkFragment();
				transaction.replace(R.id.fgShowArea, animalParkFragment);
			} else {
				transaction.show(animalParkFragment);
			}
			break;
		case 7:
			Bundle bundle = new Bundle();
			bundle.putString("videopath", data);

			if (playerFragment == null) {
				playerFragment = new PlayerFragment();
				transaction.replace(R.id.fgShowArea, playerFragment);
			} else {
				transaction.show(playerFragment);
			}
			playerFragment.setArguments(bundle);
			break;
		case 11:
			if (tPFFragment == null) {
				tPFFragment = new ThemePlayFragment();
				transaction.replace(R.id.fgShowArea, tPFFragment);
			} else {
				transaction.show(tPFFragment);
			}
			break;
		case 15:

			if (checkpointFragment == null) {
				checkpointFragment = new CheckpointFragment();
				transaction.replace(R.id.fgShowArea, checkpointFragment);
			} else {
				transaction.show(checkpointFragment);
			}

			break;
		default:
			break;
		}

		transaction.commit();
	}

	public void hideFragments() {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (tPFFragment != null) {
			transaction.hide(tPFFragment);
		}
		if (animalParkFragment != null) {
			transaction.hide(animalParkFragment);
		}
		if (playerFragment != null) {
			transaction.hide(playerFragment);
		}
		if (checkpointFragment != null) {
			transaction.hide(checkpointFragment);
		}

		transaction.commit();
	}

	boolean isFirst = true;
	boolean isFirstT = true;

	ProgressDialog mDialog;

	/**
	 * 在页面初始化后开始加载课本
	 */
	private void initBook() {
		if (mDialog == null) {
			mDialog = new ProgressDialog(this);
			mDialog.setMessage("加载中...");
			mDialog.setCancelable(false);
			mDialog.setCanceledOnTouchOutside(false);
		}

		// Log.i(TAG, "App.ketangPath:"+App.ketangPath);
		// Log.i(TAG, "App.temp_bookname:"+App.temp_bookname);
		txv_bookname.setText(App.temp_bookname);
		InitBookUtil.InitImage(App.ketangPath + App.temp_bookname + "/book/");
		// Log.i(TAG, "App.BookMuluList:"+App.BookMuluList);
		if (App.BookMuluList.size() <= 0) {
			if (isFirstT) {
				TS.show("无法为您自动加载,请手动在按钮:\"课本\"中选择");
				isFirstT = false;
			}
		} else {
			if (isFirst) {
				initImage(App.BookMuluList.get(0));
			}
		}
	}

	final String TAG = "NewPaintActivity1";

	public void doCBook(View view) {

		App.isCheck2 = true;
		Intent intent = new Intent(this, DialogStartClassGV.class);
		Bundle bundle = new Bundle();
		bundle.putInt("type", 4);
		intent.putExtras(bundle);
		startActivityForResult(intent, 10001);
	}

	Bitmap mOpenBitmap;
	Handler imgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			isFirst = false;
			if (mDialog != null)
				mDialog.dismiss();

			if (msg.what == 0) {
				paintView.undo(Integer.MAX_VALUE);
				updateUndoButton();
				paintView.open(mOpenBitmap);
			}

		}
	};

	/**
	 * 加载大图片 统一使用线程来load 然后在 设置到 PiantView中
	 * 
	 * @param name
	 *            要加载底层图片的名字
	 * 
	 * @see {@link #doNext(View)}
	 * @see {@link #doClear(View)}
	 * @see {@link #onActivityResult(int, int, Intent)}
	 * @see {@link #doLast(View)}
	 */
	private void initImage(final String name) {
		mDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (App.BookMuluList.size() > 0) {
					openIMG(App.ketangPath + App.temp_bookname + "/book/" + name);
				} else {
					imgHandler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void showMessage(String message) {
		String versionName = "";

		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
		}

		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false);
		ad.setMessage(message);
		ad.setIcon(R.drawable.ic_launcher);
		ad.setTitle(getResources().getText(R.string.app_name) + " " + versionName);
		ad.setButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ad.show();
	}

	private void updateUndoButton() {
		if (button_undo != null) {
			button_undo.setText(getResources().getText(R.string.undo) + "\n(" + paintView.getHistorySteps() + ")");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			// mediaPlayer.pause();
			button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_stop));
		} else {
			button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
		}

		// button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
		// button_play.setText("播放");
		// SocketService.SendMSG("baiping");//fragment的嵌入问题

	}

	private Handler handler;
	ArrayList<Obj_Student> stu_list = null;

	private void doQueryStudent() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cdId", App.temp_cdId);
				// 根据班级编号，获取底下所有学生
				String result = NetService.GetResultStr("CWT_QueryClassDiscussUserByCdId", map);
				if (!result.equals("")) {

					Message msg = handler.obtainMessage();
					msg.what = 1;
					Bundle data = new Bundle();
					data.putString("json", result);
					msg.setData(data);
					handler.sendMessage(msg);

				} else {
					handler.sendEmptyMessage(0);
				}

			}
		}).start();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);

				switch (msg.what) {

				case 0:

					break;
				case 1:

					String json = msg.getData().getString("json");

					try {

						stu_list = new ArrayList<Obj_Student>();

						JSONArray ja = new JSONArray(json);

						for (int i = 0; i < ja.length(); i++) {

							Obj_Student obj = (Obj_Student) MyGson.JsonToObj(ja.get(i).toString(), new Obj_Student());

							stu_list.add(obj);

						}

						doSQL();

						System.out.println("更新完成");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				// case 2:
				//
				// Intent intent = new Intent();
				//
				// intent.setClass(packageContext, cls);
				//
				// intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				//
				// startActivity(intent);
				// //
				// // int taskId = getTaskId();
				//
				// // activityManger.moveTaskToFront(taskId,
				// // ActivityManager.MOVE_TASK_NO_USER_ACTION);
				// //
				// // List<RunningAppProcessInfo> processlist =
				// // activityManger.getRunningAppProcesses();sss
				//
				// handler.sendEmptyMessageDelayed(3, 500);// 确保能关掉游戏的应用程序
				//
				// break;
				// case 3:
				//
				// endGame("com.fxyy.basicgameplugClass");
				//
				// break;

				default:
					break;

				}

			}
		};
	}

	// public void endGame(String packName) {
	//
	// List<ActivityManager.RunningAppProcessInfo> list =
	// activityManger.getRunningAppProcesses();
	//
	// for (int i = 0; i < list.size(); i++) {
	//
	// ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
	//
	// if (apinfo.processName.equals(new String(packName))) {
	//
	// String[] pkgList = apinfo.pkgList;
	//
	// for (int j = 0; j < pkgList.length; j++) {
	//
	// // 2.2以上是过时的,请用killBackgroundProcesses代替
	//
	// activityManger.killBackgroundProcesses(pkgList[j]);
	// }
	// }
	// }
	//
	// }

	/**
	 * 开个线程跑数据库操作
	 */
	private void doSQL() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AddClassFromStudent();
				AddStudentUserinfo();
			}
		}).start();
	}

	/**
	 * 添加班级中的学生
	 */
	private void AddClassFromStudent() {

		// 清空本地数据库中的 该班级下的学生
		DatabaseHelper dbHelper = new DatabaseHelper(this, App.DB_NAME);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {

			db.execSQL("delete from [CTY_ClassDiscussUser] where cdId  ='" + stu_list.get(0).getCdId() + "'");
			System.out.println(stu_list.get(0).getCdId());

		} catch (Exception e) {
			System.out.println("AddClassFromStudent1:" + e.getMessage());
		}

		// 重新向该班级中插入学生
		for (int i = 0; i < stu_list.size(); i++) {

			try {

				db.execSQL(
						SingleSql(stu_list.get(i).getCduId(), stu_list.get(i).getStuId(), stu_list.get(i).getCdId()));

			} catch (Exception e) {
				System.out.println("AddClassFromStudent2:" + e.getMessage());
			}
		}
		db.close();
	}

	private String SingleSql(String cduId, String uId, String cdId) {
		String sql = String.format("Insert Into [CTY_ClassDiscussUser] values ('%s','%s','%s');", cduId, uId, cdId);
		return sql;
	}

	private void AddStudentUserinfo() {
		DatabaseHelper dbHelper = new DatabaseHelper(this, App.DB_NAME);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for (int i = 0; i < stu_list.size(); i++) {
			try {
				db.execSQL(SingleSqlForStudent(stu_list.get(i).getStuId(), stu_list.get(i).getStuName(),
						stu_list.get(i).getLoginName(), stu_list.get(i).getClassScore() + "",
						stu_list.get(i).getCduStatus()));
			} catch (Exception e) {
				if (e.getMessage().equals("column uId is not unique (code 19)")) {
					String update = "update [CTY_UserInfo] set [uName]='" + stu_list.get(i).getStuName()
							+ "' , [LoginName]='" + stu_list.get(i).getLoginName() + "',[uScore]='"
							+ stu_list.get(i).getClassScore() + "" + "',[Status]='" + stu_list.get(i).getCduStatus()
							+ "' where [uId]='" + stu_list.get(i).getStuId() + "'";
					try {
						db.execSQL(update);
						System.out.println("更新完成");
					} catch (Exception e2) {
						System.out.println("更新失败");
					}

				}
			}
		}
		db.close();

	}

	/**
	 * 添加学生的信息 uid 学生ID 姓名
	 * 
	 * @param uId
	 * @param name
	 * @return
	 */
	private String SingleSqlForStudent(String uId, String name, String LoginName, String uScore, int Status) {
		String sql = String.format(
				"Insert Into [CTY_UserInfo]([uId],[uName],[LoginName],[uScore],[Status]) values ('%s','%s','%s',%s,'%s');",
				uId, name, LoginName, uScore, Status);
		return sql;
	}

	private void doFocuse(ListView listView) {
		listView.setFocusable(true);
		listView.requestFocus();
		listView.setFocusableInTouchMode(true);
	}

	/**
	 * @param list
	 *            该参数为跳转非第一关调用，否则设置为null
	 * @param arg2
	 *            该参数为listView item设置，不是ListView设为0
	 * @param isFirstPc
	 *            是否为第一关的pc
	 * @param isNeedLsv
	 */
	private void jumpToCp(final List<String[]> list, int arg2, boolean isFirstPc, boolean isNeedLsv) {

		Intent intent;

		if (isFirstPc) {
			CP_Constant.setCpCurrent(0);
		}

		CP_Constant.setBook(App.book);
		CP_Constant.setKetangPath(App.ketangPath);
		CP_Constant.setTemp_bookname(App.temp_bookname);
		CP_Constant.setCallbackIntent(new CPCallbackIntent() {

			@Override
			public void callback(Intent intent) {
				// 注释的代码中通常你是项目中常常用的东西
				if (intent.getAction().equals(CheckpointActivity.LOAD_CP)) {

					String choice = intent.getStringExtra("obj");

					if (choice != null) {

						SocketService.SendMSG(choice);

					}

				} else if (intent.getAction().equals(CheckpointActivity.CLOSE_CP)) {

					SocketService.SendMSG("checkpoint-close");

				} else if (intent.getAction().equals(CheckpointActivity.OPEN_ANSWER)) {

					App.temp_answer = intent.getExtras().getString("obj");

					startActivity(new Intent(NewPaintActivity1.this, DialogAnswer.class));

					// Log.i(TAG, "3:" +
					// intent.getExtras().getString("obj"));

				} else if (intent.getAction().equals(CheckpointActivity.OPEN_ANSWER)) {

					App.temp_answer = intent.getExtras().getString("obj");

					startActivity(new Intent(NewPaintActivity1.this, DialogAnswer.class));

				} else if (intent.getAction().equals(CheckpointActivity.OPEN_GD)) {

					String qu = intent.getStringExtra("qu");

					String jie = intent.getStringExtra("jie");

					if (qu != null) {

						intent = new Intent(NewPaintActivity1.this, TPlayActivity.class);

						Bundle bundle = new Bundle();

						bundle.putInt("jie", Integer.parseInt(jie));

						bundle.putInt("qu", Integer.parseInt(qu));

						intent.putExtras(bundle);

						if (intent != null) {

							startActivity(intent);

						}
					}
				} else if (intent.getAction().equals(CheckpointActivity.OPEN_THEME)) {
					// 打开相应的主题创作页面

					String note = intent.getExtras().getString("obj"); // 这个是第几个主题创作

					int num = Integer.parseInt(note);

					switch (num) {

					case 1:

						Intent intent2 = new Intent(NewPaintActivity1.this, Theme13Activity.class);

						startActivity(intent2);

						break;

					default:

						break;
					}

				} else if (intent.getAction().equals(CheckpointActivity.OPEN_GAME)) {

					String choice = intent.getStringExtra("obj");

					if (choice != null) {

						SocketService.SendMSG(choice + "-game");

						startGame("com.fxyy.basicgameplugClass");

					}

				} else if (intent.getAction().equals(CheckpointActivity.CLOSE_GAME)) {

					SocketService.SendMSG("CloseGame");

				} else if (intent.getAction().equals(CheckpointActivity.CLEAR_ANSWER)) {

					App.list_answer.removeAll(App.list_answer);

				}
			}

		});

		if (!isFirstPc) {
			try {

				// String name = list.get(arg2)[1];

				if (list.get(arg2).length > 2) {

					String index = list.get(arg2)[2].replace("\r", "");

					CP_Constant.setCpCurrent(Integer.parseInt(index) - 1);

				} else {

					CP_Constant.setCpCurrent(0);

				}

			} catch (Exception e) {

				CP_Constant.setCpCurrent(0);

			}
		}

		if (isNeedLsv) {
			intent = new Intent(NewPaintActivity1.this, CheckpointActivity.class);
			startActivity(intent);
		} else {
			setTabSelection(15, null);
		}
	}

	/**
	 * 根据配置文件做具体的跳转
	 * 
	 * @param path
	 *            文件夹路径
	 * @param list
	 *            配置文件内容列表
	 * @param arg2
	 *            在配置文件的某一行
	 * @param isNeedLsv
	 *            用来辨别是否做一些其它的操作
	 */
	private void doDetailOperation(final String path, final List<String[]> list, int arg2, boolean isNeedLsv) {

		Intent intent;

		Bundle bundle;

		int type = Integer.parseInt(list.get(arg2)[0]);

		/**
		 * 文件名
		 */
		String filepath = list.get(arg2)[1];

		qumu = filepath;

		/*** 文件路径 */
		String fileP = path + filepath;
		// fileP = fileP.substring(0, fileP.length() - 1);// 删除多出的\r(回车)

		fileP = fileP.replace("\r", "");

		File file = new File(fileP);

		if (file.exists()) {

		} else {

			if (type == -1 || type == 11 || type == 12 || type == 13 || type == 14 || type == 15 || type == 16
					|| type == 20) {

			} else {
				TS.show("这节课没有这个内容哦");
				return;
			}

		}

		switch (type) {
		case -1:// TODO 只有第一节有
			if (!isNeedLsv) {
				setTabSelection(-1, null);
			} else {
				startActivity(new Intent(NewPaintActivity1.this, AnimalParkActivity.class));
			}

			break;
		case 1:
			openImage(fileP);
			break;
		case 2:

			bundle = new Bundle();
			bundle.putString("jie", App.temp_bookname);
			bundle.putString("qu", filepath);
			App.jie = App.temp_bookname;
			App.qu = filepath;// 这里的曲是路径

			System.out.println("App.temp_bookname:" + App.temp_bookname + "   filepath:" + filepath);

			break;
		case 3:// 打气球
				// String path = fileP;

			// h.ToChallengeMidi(getstave(path));
			break;
		case 4:// 敲砖块

			// h.ToCheckpointSingle();// 挑战模式
			break;
		case 5:// 曲谱考试
			break;
		case 6:// 气球大挑战
				// h.ToChallengeSingle();// 挑战模式
			break;
		case 7:
			if (isNeedLsv) {
				mediaStop = true;
				intent = new Intent(NewPaintActivity1.this, PlayerActivity.class);
				bundle = new Bundle();
				bundle.putString("videopath", fileP);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				setTabSelection(7, fileP);
			}
			break;
		case 8:
			try {
				if (mediaPlayer != null) {
					mediaPlayer.stop();
					mediaPlayer.release();
				}
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(fileP);
				mediaPlayer.prepare();
				mediaPlayer.start();
				currentS = 0;
				App.tempMusicPath = fileP;
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						// 图标状态修改
						button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_play));
						// button_play.setText("播放");
					}
				});
				button_play.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_stop));
				// button_play.setText("暂停");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 9:
			intent = new Intent(NewPaintActivity1.this, PlayerActivity.class);
			bundle = new Bundle();
			bundle.putString("videopath", fileP);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case 10:
			// TODO CardActivity
			intent = new Intent(NewPaintActivity1.this, CardActivityCompat.class);
			startActivity(intent);
			break;
		case 11:
			if (isNeedLsv) {
				startThemesPlayActivity();
			} else {
				setTabSelection(11, null);
			}
			// doQumu(); //测试版，准备开始。
			break;
		case 12:
			startPhotoViewerActivity(12);
			break;
		case 13:
			startPhotoViewerActivity(13);
			break;
		case 14:
			startPhotoViewerActivity(14);
			break;
		case 15:

			jumpToCp(list, arg2, true, isNeedLsv);

			break;

		case 16:

			jumpToCp(list, arg2, false, isNeedLsv);

			break;
		case 20:

			jumpToCp(list, arg2, false, isNeedLsv);

			break;

		}
	}

}
