package co.legaspi.httptools;

import java.util.Random;

public final class HelperMethods {

    private HelperMethods() {
        // don't instantiate a helper class.
    }

    private static final String[] LIPSUM = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            "Phasellus dictum nunc ut tellus suscipit porta",
            "Proin vel urna eu enim consequat malesuada quis a lorem",
            "Curabitur blandit accumsan arcu, eget ornare nibh iaculis ut",
            "diam in erat lectus, eu rutrum tortor",
            "Fusce auctor tellus est, eget consectetur ante",
            "Ut rutrum mattis velit, eget accumsan tellus tempus ut",
            "Pellentesque bibendum tincidunt ligula, ut pulvinar lacus pellentesque vitae",
            "Donec sit amet mi ac est euismod aliquam",
            "Nam imperdiet lectus a lorem pulvinar quis auctor erat tincidunt",
            "Vivamus gravida diam vestibulum risus placerat pulvinar",
            "Maecenas vehicula felis ac dui semper at vehicula nisi suscipit",
            "Suspendisse ac tellus ac justo mattis mollis",
            "Integer aliquam eget mi convallis fermentum",
            "Sed eros risus, sagittis a blandit eu, tincidunt a augue",
            "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices",
            "Spam Musubi + MSG - 25% sodium = Yummy"
    };

    /**
     * Generate quasi-random 'lipsum' phrase from a static list of phrases
     *
     * @return A String containing a quasi-random phrase
     */
    public static String randomLipsum() {
        return LIPSUM[randomInt(LIPSUM.length)];
    }

    /**
     * Generate Random int from zero to max
     *
     * @param max Highest random int to generate (minus 1?)
     * @return Random int
     */
    public static int randomInt(int max) {
        Random generator = new Random();
        return generator.nextInt(max);
    }
}
