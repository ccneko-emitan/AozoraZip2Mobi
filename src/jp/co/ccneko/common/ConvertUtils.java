package jp.co.ccneko.common;

import static jp.co.ccneko.common.Contants.BOOKS_FOLDER_NAME;
import static jp.co.ccneko.common.Contants.CARDS_FOLDER_NAME;
import static jp.co.ccneko.common.Contants.EPUB_FILE_EXT;
import static jp.co.ccneko.common.Contants.EPUB_FOLDER_NAME;
import static jp.co.ccneko.common.Contants.MOBI_FILE_EXT;
import static jp.co.ccneko.common.Contants.MOBI_FOLDER_NAME;
import static jp.co.ccneko.common.Contants.ZIP_FILE_EXT;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class ConvertUtils {
	
	private ConvertUtils() {}
	
	public static void convertAllFile(String[] args, ConvertType convertType){
		// check cards
		File cards = null;
		Path cardsPath = null;
		File outputPath = null;
		if(ConvertType.ZIP2EPUB == convertType || ConvertType.ZIP2EPUB2MOBI == convertType){
			cards = new File(args[0], CARDS_FOLDER_NAME);
			if(!cards.exists()){
				System.err.println("The folder is not exist: " + cards.getAbsolutePath());
				return;
			}
			if(!cards.isDirectory()){
				System.err.println(cards.getAbsolutePath() + " is not a folder");
				return;
			}
			cardsPath = cards.toPath();
			System.out.println("The path of cards: " + cardsPath);

			// check output folder
			outputPath = new File(args[1], BOOKS_FOLDER_NAME);
		}else{
			// check output folder
			outputPath = new File(args[0], BOOKS_FOLDER_NAME);
		}
		if(!outputPath.exists()){
			outputPath.mkdirs();
		}
		if(!outputPath.isDirectory()){
			System.err.println(outputPath.getAbsolutePath() + " is not a folder");
			return;
		}
		System.out.println("The path of output files: " + outputPath);
		convertZip2EBook(cardsPath, outputPath.toPath(), convertType);
	}

    /**
     * チェック処理を行う。
     * @return エラー情報リスト
     */
	public static void convertZip2EBook(Path cardsPath, Path outputPath, ConvertType convertType){
		// convert zip to epub
		if(ConvertType.ZIP2EPUB == convertType || ConvertType.ZIP2EPUB2MOBI == convertType){
			try (Stream<Path> stream = Files.walk(cardsPath, Integer.MAX_VALUE)){
				stream.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().toLowerCase().endsWith(ZIP_FILE_EXT))
					.forEach(p -> convertZip2Epub(p, outputPath));
			} catch (IOException e) {
				System.err.println("Error occurred when visiting the 「cards」 folder.");
				return;
			}
		}

		if(ConvertType.EPUB2MOBI == convertType || ConvertType.ZIP2EPUB2MOBI == convertType){
			// convert epub to mobi
			try (Stream<Path> stream = Files.walk(outputPath, 1)){
				stream.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().toLowerCase().endsWith(EPUB_FILE_EXT))
					.forEach(ConvertUtils::convertEpub2Mobi);
			} catch (IOException e) {
				System.err.println("Error occurred when visiting the 「cards」 folder.");
				return;
			}
		}
	}
	
	public static void convertEpub2Mobi(Path epubPath){
		String epubFileName = epubPath.getFileName().toString();
		String mobiFileName = epubFileName.replaceAll(EPUB_FILE_EXT, MOBI_FILE_EXT);
		String author = epubFileName.substring(epubFileName.indexOf('[') + 1, epubFileName.indexOf(']'));
		Path mobiPath = new File(epubPath.getParent().toString(), mobiFileName).toPath();
		
		if(author == null || author.trim().isEmpty()){
			return;
		}
		
		// convert
		System.out.println("Convert to mobi: " + epubPath);
		String execCmd = "kindlegen.exe \"" + epubPath + "\"";
		doExec(execCmd, "UTF-8");


		// create epub folder
		System.out.println("create epub folder");
		File epubFolder = new File(new File(epubPath.getParent().toString(), EPUB_FOLDER_NAME), author);
		if(!epubFolder.exists()){
			epubFolder.mkdirs();
		}
		// move to author folder
		System.out.println("move epub file to author folder");
		try {
			if(epubPath.toFile().exists()){
				Files.move(epubPath, new File(epubFolder, epubFileName).toPath());
			}
		} catch (Exception e) {
			System.err.println("Fail to move " + epubPath + " to " + epubFolder);
			e.printStackTrace();
		}
		// create mobi folder
		System.out.println("mobi folder");
		File mobiFolder = new File(new File(epubPath.getParent().toString(), MOBI_FOLDER_NAME), author);
		if(!mobiFolder.exists()){
			mobiFolder.mkdirs();
		}
		// move to author folder
		System.out.println("move mobi file to author folder");
		try {
			if(mobiPath.toFile().exists()){
				Files.move(mobiPath, new File(mobiFolder, mobiFileName).toPath());
			}
		} catch (Exception e) {
			System.err.println("Fail to move " + mobiPath + " to " + mobiFolder);
			e.printStackTrace();
		}
	}
	
	public static void convertZip2Epub(Path path, Path outputPath){
		System.out.println("Convert to epub: " + path);
		String execCmd = "java -cp AozoraEpub3.jar AozoraEpub3 -d \"" + outputPath + "\" \"" + path + "\"";
		doExec(execCmd, "Shift-JIS");
	}
	
	public static void doExec(String execCmd, String logCharsetName){
		System.out.println(execCmd);
		try {
			Process process = Runtime.getRuntime().exec(execCmd);
			InputStream is1 = process.getInputStream();   
			InputStream is2 = process.getErrorStream(); 
			
			new Thread(){
				@Override
				public void run(){
					try{
						BufferedReader in = null;
						if(logCharsetName == null){
							in = new BufferedReader(new InputStreamReader(is1));
						}else{
							in = new BufferedReader(new InputStreamReader(is1, logCharsetName));
						}
						String line = null;
						while((line = in.readLine()) != null){
							System.out.println(line);
						}
					} catch (IOException e){
						System.err.println("Error occurred : " + execCmd);
						e.printStackTrace();
					} finally{
						try {
							is1.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			
			new Thread(){
				@Override
				public void run(){
					try{
						BufferedReader in = null;
						if(logCharsetName == null){
							in = new BufferedReader(new InputStreamReader(is2));
						}else{
							in = new BufferedReader(new InputStreamReader(is2, logCharsetName));
						}
						String line = null;
						while((line = in.readLine()) != null){
							System.err.println(line);
						}
					} catch (IOException e){
						System.err.println("Error occurred : " + execCmd);
						e.printStackTrace();
					} finally{
						try {
							is2.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			
			System.out.println("waiting for");
			process.waitFor();
			process.destroy();
		} catch (Exception e) {
			System.err.println("Error occurred : " + execCmd);
			e.printStackTrace();
		}
	}

}
