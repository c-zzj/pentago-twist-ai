package autoplay;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

//Author: Lilly Tong, Eric Crawford
//
// Assumes all the code in ``src`` has been compiled, and the resulting
// class files were stored in ``bin``.
//
// From the root directory of the project, run
//
//     java -cp bin autoplay.Autoplay n_games
//
// Note: The script is currently set up to have the StudentPlayer play against
// RandomHusPlayer. In order to have different players participate, you need
// to change the variables ``client1_line`` and ``client2_line``. Make sure
// that in those lines, the classpath and the class name is set appropriately
// so that java can find and run the compiled code for the agent that you want
// to test. For example to have StudentPlayer play against itself, you would
// change ``client2_line`` to be equal to ``client1_line``.
//
public class Autoplay {
	public static void main(String args[]) {
		String n_games = "100";
		String[] input = {n_games};
		
		int[][] results = new int[10][5]; // 0-win1w; 1-win1b; 2-win2w; 3-win2b; 4-draws
		for(int i=0; i<1;i++)
		{
			results[i] = simulate(input);
		}
		System.out.println(Arrays.deepToString(results));
	}

    public static int[] simulate(String args[]) {
        int n_games;
        try {
            n_games = Integer.parseInt(args[0]);
            if (n_games < 1) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.err.println(
                    "First argument to Autoplay must be a positive int " + "giving the number of games to play.");
            return null;
        }

        try {
            ProcessBuilder server_pb = new ProcessBuilder("java", "-cp", "bin", "boardgame.Server", "-ng", "-k");
            server_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            Process server = server_pb.start();

            ProcessBuilder client1_pb = new ProcessBuilder("java", "-cp", "bin", "-Xms520m", "-Xmx520m",
                    "boardgame.Client", "student_player.StudentPlayer");
            client1_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            ProcessBuilder client2_pb = new ProcessBuilder("java", "-cp", "bin", "-Xms520m", "-Xmx520m",
                    "boardgame.Client", "student_player.AdversaryPlayer");
            //pentago_twist.RandomPentagoPlayer
            //student_player.AdversaryPlayer
            //student_player.StudentPlayer
            //student_player_tian.StudentPlayer
            client2_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            int win1w = 0, win2w = 0, win1b = 0, win2b = 0, draw = 0;
            for (int i = 0; i < n_games; i++) {
                System.out.println("Game " + i);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                Process client1 = ((i % 2 == 0) ? client1_pb.start() : client2_pb.start());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                Process client2 = ((i % 2 == 0) ? client2_pb.start() : client1_pb.start());

                try {
                    client1.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    client2.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                RandomAccessFile br = new RandomAccessFile(
                		"C:\\Users\\Christian\\OneDrive\\XZ's OneDrive\\OneDrive\\Desktop\\Workspace-on-OneDrive\\pentago_twist-main\\src\\autoplay\\out.txt"
                		,"r");
                br.skipBytes((int)br.length()-3);
    			int lastChar = br.read();
                if (lastChar=='0')
                {
                	if (i%2==0) win1w++;
                	else win2w++;
                }
                else if(lastChar=='1')
                {
                	if (i%2==0) win2b++;
                	else win1b++;
                }
                else draw++;
                br.close();
                
                System.out.println("Player 1 won "+win1w+" games playing WHITE");
                System.out.println("Player 1 won "+win1b+" games playing BLACK");
    			System.out.println("Player 2 won "+win2w+" games playing WHITE");
    			System.out.println("Player 2 won "+win2b+" games playing BLACK");
    			System.out.println("There are "+draw+" draws");
            }
            server.destroy();
            int[] result = new int[5];
            result[0] = win1w;
            result[1] = win1b;
            result[2] = win2w;
            result[3] = win2b;
            result[4] = draw;
            return result;

        } catch (IOException e) {
            e.printStackTrace(); return null;
        }
    }
}