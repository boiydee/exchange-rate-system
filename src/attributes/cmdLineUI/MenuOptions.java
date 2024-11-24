package attributes.cmdLineUI;

/**
 * Holds the different options for the menu
 */
public enum MenuOptions {


    GETOUTGOINGTRANSFERREQUESTS("View your outgoing transfer requests"),

    GETINCOMINGTRANSFERREQUESTS("View your incoming transfer requests"),
    GETCURRENTUSERINFO("View account info"),
    GETCURRENTEXCHANGERATES("View current exchange rates"),
    GETONLINEUSERS("View Online Users"),
    SENDTRANSFERREQUESTS("Send new transfer request"),

    EXIT("Quit");

    private final String description;

    MenuOptions(String description) {
        this.description = description;

    }

    public String getDescription() {
        return description;
    }
}