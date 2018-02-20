import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import jp.co.ccneko.common.ConvertType;
import jp.co.ccneko.common.ConvertUtils;

public class AozoraZip2Mobi {

	public static void main(String[] args) {
		String helpMsg = "java -jar AozoraZip2Mobi.jar [-options]";
		
		Options options = new Options();
		options.addOption("c", "cards", true, "入力フォルダのパス（「cards」のパス）");
		options.addOption("b", "books", true, "出力先パスのパス（「books」のパス）");
		options.addOption("t", "convertType", true, "変換タイプ（1:zip->epub, 2:zip->epub->mobi, 3:epub->mobi）");
		
		CommandLine commandLine;
		try {
			System.out.println("CommandLine Parsing...");
			commandLine = new DefaultParser().parse(options, args, true);
		} catch (Exception e) {
			new HelpFormatter().printHelp(helpMsg, options);
			return;
		}
		if (!commandLine.hasOption('t') ) {
			System.err.println("Please input the convert type:1:zip->epub, 2:zip->epub->mobi, 3:epub->mobi.");
			new HelpFormatter().printHelp(helpMsg, options);
			return;
		}
		ConvertType convertType = ConvertType.getConvertType(commandLine.getOptionValue('t'));
		String[] convertArgs = null;
		if (convertType == null) {
			System.err.println("Please input the right convert type:1:zip->epub, 2:zip->epub->mobi, 3:epub->mobi.");
			return;
		}
		if (!commandLine.hasOption('b') ) {
			System.err.println("Please input the path of output files.");
			new HelpFormatter().printHelp(helpMsg, options);
			return;
		}
		if(ConvertType.ZIP2EPUB == convertType || ConvertType.ZIP2EPUB2MOBI == convertType){
			if (!commandLine.hasOption('c') ) {
				System.err.println("Please input the path of cards folder.");
				new HelpFormatter().printHelp(helpMsg, options);
				return;
			}
			convertArgs = new String[2];
			convertArgs[0] = commandLine.getOptionValue('c');
			convertArgs[1] = commandLine.getOptionValue('b');
		} else {
			convertArgs = new String[1];
			convertArgs[0] = commandLine.getOptionValue('b');
		}
		
		
		ConvertUtils.convertAllFile(convertArgs, convertType);
	}
	
}
