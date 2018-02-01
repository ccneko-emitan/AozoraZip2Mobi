package jp.co.ccneko.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class AozoraZip2Mobi {
	
	private final static String CARDS_FOLDER_NAME = "cards";
	
	private final static String BOOKS_FOLDER_NAME = "books";
	
	private final static String EPUB_FOLDER_NAME = "epub";
	
	private final static String MOBI_FOLDER_NAME = "mobi";
	
	private final static String ZIP_FILE_EXT = ".zip";
	
	private final static String EPUB_FILE_EXT = ".epub";
	
	private final static String MOBI_FILE_EXT = ".mobi";

	public static void main(String[] args) {
		// Get the path of cards from arguments
		if(args.length < 2){
			System.err.println("Please input the common line like this:");
			System.err.println("AozoraZip2Mobi \"cardsPath\" \"outputPath\"");
			return;
		}

		convertAllFile(args);
	}
	
	private static void convertAllFile(String[] args){
		// check cards
		File cards = new File(args[0], CARDS_FOLDER_NAME);
		if(!cards.exists()){
			System.err.println("The folder is not exist: " + cards.getAbsolutePath());
			return;
		}
		if(!cards.isDirectory()){
			System.err.println(cards.getAbsolutePath() + " is not a folder");
			return;
		}
		// check output folder
		File outputPath = new File(args[1], BOOKS_FOLDER_NAME);
		if(!outputPath.exists()){
			outputPath.mkdirs();
		}
		if(!outputPath.isDirectory()){
			System.err.println(outputPath.getAbsolutePath() + " is not a folder");
			return;
		}
		
		Path cardsPath = cards.toPath();
		System.out.println("The path of cards: " + cardsPath);
		System.out.println("The path of output files: " + outputPath);
		convertZip2EBook(cardsPath, outputPath.toPath());
	}
	
	private static void convertZip2EBook(Path cardsPath, Path outputPath){
		// convert zip to epub
		try (Stream<Path> stream = Files.walk(cardsPath, Integer.MAX_VALUE)){
			stream.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().toLowerCase().endsWith(ZIP_FILE_EXT))
				.forEach(p -> convertZip2Epub(p, outputPath));
		} catch (IOException e) {
			System.err.println("Error occurred when visiting the 「cards」 folder.");
			return;
		}
		
		// convert epub to mobi
		try (Stream<Path> stream = Files.walk(outputPath, 1)){
			stream.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().toLowerCase().endsWith(EPUB_FILE_EXT))
				.forEach(AozoraZip2Mobi::convertEpub2Mobi);
		} catch (IOException e) {
			System.err.println("Error occurred when visiting the 「cards」 folder.");
			return;
		}
	}
	
	private static void convertEpub2Mobi(Path epubPath){
		String epubFileName = epubPath.getFileName().toString();
		String mobiFileName = epubFileName.replaceAll(EPUB_FILE_EXT, MOBI_FILE_EXT);
		String author = epubFileName.substring(epubFileName.indexOf('[') + 1, epubFileName.indexOf(']'));
		Path mobiPath = new File(epubPath.getParent().toString(), mobiFileName).toPath();
		
		// convert
		System.out.println("Convert to mobi: " + epubPath);
		String execCmd = "kindlegen.exe \"" + epubPath + "\"";
		doExec(execCmd, "UTF-8");


		// create epub folder
		File epubFolder = new File(new File(epubPath.getParent().toString(), EPUB_FOLDER_NAME), author);
		if(!epubFolder.exists()){
			epubFolder.mkdirs();
		}
		// move to author folder
		try {
			if(epubPath.toFile().exists()){
				Files.move(epubPath, new File(epubFolder, epubFileName).toPath());
			}
		} catch (IOException e) {
			System.err.println("Fail to move " + epubPath + " to " + epubFolder);
			e.printStackTrace();
		}
		// create mobi folder
		File mobiFolder = new File(new File(epubPath.getParent().toString(), MOBI_FOLDER_NAME), author);
		if(!mobiFolder.exists()){
			mobiFolder.mkdirs();
		}
		// move to author folder
		try {
			if(mobiPath.toFile().exists()){
				Files.move(mobiPath, new File(mobiFolder, mobiFileName).toPath());
			}
		} catch (IOException e) {
			System.err.println("Fail to move " + mobiPath + " to " + mobiFolder);
			e.printStackTrace();
		}
	}
	
	private static void convertZip2Epub(Path path, Path outputPath){
		System.out.println("Convert to epub: " + path);
		String execCmd = "java -cp AozoraEpub3.jar AozoraEpub3 -d \"" + outputPath + "\" \"" + path + "\"";
		doExec(execCmd, "Shift-JIS");
	}
	
	private static void doExec(String execCmd, String logCharsetName){
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
