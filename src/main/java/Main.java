import config.JarConfig;
import steps.FirstStep;
import steps.SecondStep;
import steps.ThirdStep;

import static java.lang.Integer.parseInt;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("We need some values to start.");
            System.out.println("1 - TOKEN that will be used");
            System.out.println("OR");
            System.out.println("1 - URL that will be used");
            System.out.println("2 - Token that will be used");
            System.out.println("3 - Limit for rest list's. Default will be 50");
        }

        if (args.length == 1) {
            JarConfig.setInstance("https://www.faire-stage.com/api/v1", args[0], 50);
        } else if (args.length >= 2) {
            JarConfig.setInstance(args[0], args[1], args.length >= 3 ? parseInt(args[2]) : 50);
        } else {
            System.out.println("Bye !!!");
            System.exit(0);
        }


        /** Products **/
        FirstStep.runFirstStep();
        /** Orders **/
        SecondStep.runSecondStep();
        /** Metrics **/
        ThirdStep.runThirdStep();


    }




}
