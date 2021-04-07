package sample;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;

/**
 * An interactive contact address book - starter code
 * CSCU9A2 assignment Spring 2018
 * <p>
 * THIS STARTER CODE WORKS PARTIALLY BUT SOME METHODS REQUIRE COMPLETING - SEE FURTHER DOWN IN THIS COMMENT
 * <p>
 * The contact address book contains a 'database' of names, addresses and other details
 * - quite small in this version, but in principle it could be quite large.
 * At any one time the details of just one of the contacts will be on display.
 * <p>
 * Buttons are provided to allow the user:
 * o  to step forwards and backwards through the entries in the address book
 * o  to add a new contact
 * o  to delete the current or all contacts
 * o  to search for a contact by exact name match
 * o  to search for a contact by case insensitive match to any part of a name
 * o  to re-order the contacts in ascending name order
 * o  to re-order the contacts in descending name order
 * <p>
 * Of course, in this exercise, the 'database' is a little unrealistic:
 * the information is built-in to the program (whereas in a 'serious' system it would,
 * perhaps, be read in from a file).
 * <p>
 * *** TO BE DONE: ***
 * The navigation buttons function correctly.
 * The add new contact button is ALMOST CORRECT.
 * The core data processing methods for the other six buttons REQUIRE COMPLETING.
 * <p>
 * You should insert your student number instead of 1234567 in line 117.
 * <p>
 * All other work is on the METHOD BODIES BELOW line 445 (see /////////////////////////////):
 * <p>
 * You must complete the addContact method, and implement full method bodies for
 * deleteContact, clearContacts, findContact, findPartial, sortAtoZ and sortZtoA.
 * <p>
 * You MUST NOT alter any other parts of the program:
 * o  the GUI parts are complete and correct
 * o  the array declarations are complete and correct
 * o  the method headers are complete and correct.
 *
 * @author sbj
 * @version March 2018
 */
public class AddressBook extends JFrame implements ActionListener {
    /**
     * Configuration: custom screen colours, layout constants and custom fonts.
     */
    private final Color
            veryLightGrey = new Color(240, 240, 240),
            darkBlue = new Color(0, 0, 150),
            backGroundColour = veryLightGrey,
            navigationBarColour = Color.lightGray,
            textColour = darkBlue;
    private static final int
            windowWidth = 450, windowHeight = 600,               // Overall frame dimensions
            windowLocationX = 200, windowLocationY = 100;        //     and position
    private final int
            panelWidth = 450, panelHeight = 250,                 // The drawing panel dimensions
            leftMargin = 50,                                     // All text and images start here
            mainHeadingY = 30,                                   // Main heading this far down the panel
            detailsY = mainHeadingY + 40,                          // Details display starts this far down the panel
            detailsLineSep = 30;                                 // Separation of details text lines
    private final Font
            mainHeadingFont = new Font("SansSerif", Font.BOLD, 20),
            detailsFont = new Font("SansSerif", Font.PLAIN, 14);

    /**
     * The navigation buttons.
     */
    private JButton
            first = new JButton("|<"),            // For "move to first contact" action
            previous = new JButton("<"),          // For "move to previous contact" action
            next = new JButton(">"),              // For "move to next contact" action
            last = new JButton(">|");             // For "move to final contact" action

    /**
     * The action buttons
     */
    private JButton
            addContact = new JButton("Add new contact"),   // To request adding a new contact
            deleteContact = new JButton("Delete contact"), // To delete the currently selected contact
            deleteAll = new JButton("Delete all"),         // To delete all contacts
            findContact = new JButton("Find exact name"),  // To find contact by exact match of name
            findPartial = new JButton("Find partial name"),// To find contact by partial, case insensitive match of name
            sortAtoZ = new JButton("Sort A to Z"),         // To request re-ordering the contact by names A to Z
            sortZtoA = new JButton("Sort Z to A");         // To request re-ordering the contacts by name Z to A

    /**
     * Text fields for data entry for adding new contact and finding a contact
     */
    private JTextField
            nameField = new JTextField(20),                // For entering a new name, or a name to find
            addressField = new JTextField(30),             // For entering a new address
            mobileField = new JTextField(12),              // For entering a new mobile number
            emailField = new JTextField(30);               // For entering a new email address

    /**
     * The contact details drawing panel.
     */
    private JPanel contactDetails = new JPanel() {
        // paintComponent is called automatically when a screen refresh is needed
        public void paintComponent(Graphics g) {
            // g is a cleared panel area
            super.paintComponent(g); // Paint the panel's background
            paintScreen(g);          // Then the required graphics
        }
    };

    /**
     * The main program launcher for the AddressBook class.
     *
     * @param args The command line arguments (ignored here).
     */
    public static void main(String[] args) {
        AddressBook contacts = new AddressBook();
        contacts.setSize(windowWidth, windowHeight);
        contacts.setLocation(windowLocationX, windowLocationY);
        contacts.setTitle("My address book: 2626628");
        contacts.setUpAddressBook();
        contacts.setUpGUI();
        contacts.setVisible(true);
    } // End of main

    /**
     * Organizes overall set up of the address book data at launch time.
     */
    private void setUpAddressBook() {
        // Set up the contacts' details in the database
        currentSize = 0;    // No contacts initially
        addContact("John", "12 Cottrell Street, Stirling", "07999232321", "john@cs.isp.com");
        addContact("Paul", "23 Beatle Street, London", "0033998877", "paul@paul.net");
        addContact("George", "34 Beatle Street, New York", "01222 78160", "georgie@stirling.com");
        addContact("Simon", "45 Pathfoot Lane, Bridge of Allan", "0999 8888", "simon@simon.net");
        addContact("Leslie", "Box 3, Glasgow", "", "");
        // currentSize should now be 5

        // Initially selected contact - the first in the database
        currentContact = 0;
    } // End of setUpAddressBook

    /**
     * Sets up the graphical user interface.
     * <p>
     * Some extra embedded JPanels are used to improve layout a little
     */
    private void setUpGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(new FlowLayout());
        window.setBackground(navigationBarColour);

        // Set up the GUI buttons
        // The widget order is:
        // first (|<), previous (<), next (>), last (>|)

        window.add(new JLabel("Navigation:"));
        window.add(first);
        first.addActionListener(this);
        window.add(previous);
        previous.addActionListener(this);
        window.add(next);
        next.addActionListener(this);
        window.add(last);
        last.addActionListener(this);

        // Set up the details graphics panel
        contactDetails.setPreferredSize(new Dimension(panelWidth, panelHeight));
        contactDetails.setBackground(backGroundColour);
        window.add(contactDetails);

        // Set up action buttons
        JPanel addDelPanel = new JPanel();
        addDelPanel.add(addContact);
        addContact.addActionListener(this);
        addDelPanel.add(deleteContact);
        deleteContact.addActionListener(this);
        addDelPanel.add(deleteAll);
        deleteAll.addActionListener(this);
        window.add(addDelPanel);
        JPanel findPanel = new JPanel();
        findPanel.add(findContact);
        findContact.addActionListener(this);
        findPanel.add(findPartial);
        findPartial.addActionListener(this);
        window.add(findPanel);
        JPanel sortPanel = new JPanel();
        sortPanel.add(sortAtoZ);
        sortAtoZ.addActionListener(this);
        sortPanel.add(sortZtoA);
        sortZtoA.addActionListener(this);
        window.add(sortPanel);

        // Set up text fields for data entry
        // (using extra JPanels to improve layout control)
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("New/find name:"));
        namePanel.add(nameField);
        window.add(namePanel);

        JPanel addressPanel = new JPanel();
        addressPanel.add(new JLabel("New address:"));
        addressPanel.add(addressField);
        window.add(addressPanel);

        JPanel mobilePanel = new JPanel();
        mobilePanel.add(new JLabel("New mobile:"));
        mobilePanel.add(mobileField);
        window.add(mobilePanel);

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("New email:"));
        emailPanel.add(emailField);
        window.add(emailPanel);
    } // End of setUpGUI

    /**
     * Display non-background colour areas, heading and currently selected database contact.
     *
     * @param g The Graphics area to be drawn on, already cleared.
     */
    private void paintScreen(Graphics g) {
        // Main heading
        g.setColor(textColour);
        g.setFont(mainHeadingFont);
        g.drawString("Contact details", leftMargin, mainHeadingY);

        // Current details
        displayCurrentDetails(g);
    } // End of paintScreen

    /**
     * Display the currently selected contact.
     *
     * @param g The Graphics area to be drawn on.
     */
    private void displayCurrentDetails(Graphics g) {
        g.setColor(textColour);
        g.setFont(detailsFont);
        if (currentContact == -1)           // Check if no contact is selected, that is there are no contacts
            g.drawString("There are no contacts", leftMargin, detailsY);
        else {   // Display selected contact
            g.drawString(name[currentContact], leftMargin, detailsY);
            g.drawString(address[currentContact], leftMargin, detailsY + detailsLineSep);
            g.drawString("Mobile: " + mobile[currentContact], leftMargin, detailsY + 2 * detailsLineSep);
            g.drawString("Email: " + email[currentContact], leftMargin, detailsY + 3 * detailsLineSep);
        }
    } // End of displayCurrentDetails

    /**
     * Handle the various button clicks.
     *
     * @param e Information about the button click
     */
    public void actionPerformed(ActionEvent e) {
        // If first is clicked: Cause the 0th contact to become selected (or -1 if there are none)
        if (e.getSource() == first)
            if (currentContact >= 0)
                currentContact = 0;
            else
                currentContact = -1;

        // If previous is clicked: Cause the previous contact to become selected, if there is one
        if (e.getSource() == previous && currentContact > 0)
            currentContact--;

        // If next is clicked: Cause the next contact to become selected, if there is one
        if (e.getSource() == next && currentContact < currentSize - 1)
            currentContact++;

        // If last is clicked: Cause the final available contact to become selected (or -1 if there are none)
        if (e.getSource() == last)
            currentContact = currentSize - 1;

        // Add a new contact
        if (e.getSource() == addContact)
            doAddContact();

        // Delete the current contact
        if (e.getSource() == deleteContact)
            doDeleteContact();

        // Delete all contacts
        if (e.getSource() == deleteAll)
            doDeleteAll();

        // Find a contact with exact name match
        if (e.getSource() == findContact)
            doFindContact();

        // Find a contact with partial, case insensitive name match
        if (e.getSource() == findPartial)
            doFindPartial();

        // Re-order the contacts by name A to Z
        if (e.getSource() == sortAtoZ)
            doSortAtoZ();

        // Re-order the contacts by name Z to A
        if (e.getSource() == sortZtoA)
            doSortZtoA();

        // And refresh the display
        repaint();
    } // End of actionPerformed

    /**
     * Add a new contact using data from the entry text fields
     * <p>
     * Only adds if the name field is not empty (other fields do not matter),
     * and if there is space in the arrays.
     * Pops up dialogue box giving reason if contact is not added.
     * The new contact is selected immediately.
     */
    private void doAddContact() {
        String newName = nameField.getText();
        nameField.setText("");
        String newAddress = addressField.getText();
        addressField.setText("");
        String newMobile = mobileField.getText();
        mobileField.setText("");
        String newEmail = emailField.getText();
        emailField.setText("");
        if (newName.length() == 0)         // Check and exit if the new name is empty
        {
            JOptionPane.showMessageDialog(null, "No name entered");
            return;
        }
        int index = addContact(newName, newAddress, newMobile, newEmail); // index is where added, or -1
        if (index == -1)                   // Check for success
            JOptionPane.showMessageDialog(null, "No space for new name");
        else
            currentContact = index;        // Immediately select the new contact
    } // End of doAddContact

    /**
     * Delete the currently selected contact
     * <p>
     * If there are no contacts, then notify the user, but otherwise no action.
     * Otherwise delete, and the following remaining contact becomes selected.
     * If there is no following contact (that is, just deleted the highest indexed contact),
     * then the previous becomes selected.
     * If there is no previous (that is, just deleted contact 0), then all contacts have
     * been deleted and so no contact is selected.
     */
    private void doDeleteContact() {
        if (currentSize == 0)               // No contacts? If so do nothing
        {
            JOptionPane.showMessageDialog(null, "No contacts to delete");
            return;
        }
        deleteContact(currentContact);
        // currentContact is OK as the selected contact index, unless:
        if (currentContact == currentSize)    // Just deleted the highest indexed contact?
            currentContact--;                 // Adjust down to previous (or -1 if all deleted)
    } // End of doDeleteContact

    /**
     * Delete all the contacts - clear the list
     */
    private void doDeleteAll() {
        clearContacts();
        currentContact = -1;    // No contact selected
    } // End of doDeleteAll

    /**
     * Search for the contact whose name is an exact match to the name given in the name text field.
     * <p>
     * The search name must not be empty.
     * If found then the contact becomes selected.
     * If not found then the user is notified, and the selected contact does not change.
     */
    private void doFindContact() {
        String searchName = nameField.getText();
        if (searchName.length() == 0)               // Check and exit if the search name is empty
        {
            JOptionPane.showMessageDialog(null, "Name must not be empty");
            return;
        }
        int location = findContact(searchName);     // Location is where found, or -1
        if (location == -1)                         // Check result: not found?
            JOptionPane.showMessageDialog(null, "Name not found");
        else {
            currentContact = location;              // Select the found contact
            nameField.setText("");                  // And clear the search field
        }
    } // End of doFindContact

    /**
     * Search for the contact whose name contains the text given in the name text field,
     * case insensitively.
     * <p>
     * The search text must not be empty.
     * If found then the contact becomes selected.
     * If not found then the user is notified, and the selected contact does not change.
     */
    private void doFindPartial() {
        String searchText = nameField.getText();
        if (searchText.length() == 0)               // Check and exit if the search text is empty
        {
            JOptionPane.showMessageDialog(null, "Search text must not be empty");
            return;
        }
        int location = findPartial(searchText);     // Location is where found, or -1
        if (location == -1)                         // Check result: not found?
            JOptionPane.showMessageDialog(null, "Name not found");
        else {
            currentContact = location;              // Select the found contact
            nameField.setText("");                  // And clear the search field
        }
    } // End of doFindPartial

    /**
     * Re-order the contacts in the database so that the names are in ascending alphabetic order
     * <p>
     * The first contact becomes selected, provided that there is one.
     */
    private void doSortAtoZ() {
        sortAtoZ();
        if (currentSize > 0)
            currentContact = 0;      // Index of the first contact
        else
            currentContact = -1;
    } // End of doSortAtoZ

    /**
     * Re-order the contacts in the database so that the names are in descending alphabetic order
     * <p>
     * The first contact becomes selected, provided that there is one.
     */
    private void doSortZtoA() {
        sortZtoA();
        if (currentSize > 0)
            currentContact = 0;      // Index of the first contact
        else
            currentContact = -1;
    } // End of doSortZtoA

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Maximum capacity of the database.
     */
    private final int databaseSize = 10;

    /**
     * To hold contacts' names, addresses, etc.
     */
    private String[]
            name = new String[databaseSize],
            address = new String[databaseSize],
            mobile = new String[databaseSize],
            email = new String[databaseSize];

    /**
     * The current number of entries - always a value in range 0 .. databaseSize.
     * <p>
     * The entries are held in elements 0 .. currentSize-1 of the arrays.
     */
    private int currentSize = 0;

    /**
     * To hold index of currently selected contact
     * <p>
     * There is always one selected contact, unless there are no entries at all in the database.
     * If there are one or more entries, then currentContact has a value in range 0 .. currentSize-1.
     * If there are no entries, then currentContact is -1.
     */
    private int currentContact = -1;

    /**
     * Add a new contact to the database in the next available location, if there is space.
     * <p>
     * Return the index where added if successful, or -1 if no space so not added.
     */
    private int addContact(String newName, String newAddress, String newMobile, String newEmail) {
        if (currentSize < databaseSize) {       //Check the space available and returns -1
            name[currentSize] = newName;
            address[currentSize] = newAddress;  //Date at first free element in each array
            mobile[currentSize] = newMobile;
            email[currentSize] = newEmail;
            currentSize++;                       // Counts one more contact
            return currentSize - 1; // Success, return where added
        } else {
            return -1;
        }
    } // End of addContact

    /**
     * Delete the indicated contact from the database
     * <p>
     * All contacts in subsequent (higher indexed) elements of the arrays are moved "down" to fill the "gap".
     * The order of the remaining contacts is unchanged (for example, if previously sorted alphabetically,
     * then so will they be after deletion).
     */
    private void deleteContact(int index) {
        for (int i = index; i < name.length - 1; i++) {
            if (null != name[i + 1]) {
                name[i] = name[i + 1];
                address[i] = address[i + 1];
                mobile[i] = mobile[i + 1];
                email[i] = email[i + 1];
            } else {
                name[i] = null;
                address[i] = null;
                mobile[i] = null;
                email[i] = null;
            }
        }
        currentSize--;
    } // End of deleteContact

    /**
     * Clear the contacts database - set to empty
     */
    private void clearContacts() {
        //fill all arrays with null value to clear contacts
        Arrays.fill(name, null);
        Arrays.fill(address, null);
        Arrays.fill(mobile, null);
        Arrays.fill(email, null);
        currentSize = 0;

    } // End of clearContacts

    /**
     * Search the database for an exact match for the given name.
     * <p>
     * Return the index of the match found, or -1 if no match found.
     */
    private int findContact(String searchName) {   //findContact by searchName
        for (int i = 0; i < name.length; i++) {
            if (searchName.equals(name[i])) {
                return i; //return the index found
            }
        }
        return -1;   //Return -1 if contact not found.
    } // End of findContact

    /**
     * Search the database for a contact whose name contains the given search text, case insensitively.
     * <p>
     * Return the index of the match found, or -1 if no match found.
     */
    private int findPartial(String searchText) {
        for (int i = 0; i < name.length; i++) {
            //partial match and case insensitive so convert to lower case and compare.
            if (name[i].toLowerCase().contains(searchText.toLowerCase())) {
                return i;
            }

        }
        return -1;   // Return where found or -1
    } // End of findPartial


    /**
     * swapEntries utility method used when sorting.
     * @param arr
     * @param i
     * @param j
     */
    private void swapEntries(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    } // End of swapEntries

    /**
     * Re-order the contacts in the database so that the names are in ascending alphabetic order
     */
    private void sortAtoZ() {   //sortAtoZ
        {
            for (int i = 0; i < currentSize; i++) {
                for (int j = i + 1; j < currentSize; j++) {
                    if (name[i].compareTo(name[j]) > 0) {
                        swapEntries(name, i, j);
                        swapEntries(address, i, j);
                        swapEntries(mobile, i, j);
                        swapEntries(email, i, j);
                    }
                }
            }
        }//End of sortAtoZ
    }

    /**
     * Re-order the contacts in the database so that the names are in descending alphabetic order
     */
    private void sortZtoA() {     //sortZtoA
        for (int i = 0; i < currentSize; i++) {
            for (int j = i + 1; j < currentSize; j++) {
                if (name[i].compareTo(name[j]) < 0) {
                    swapEntries(name, i, j);
                    swapEntries(address, i, j);
                    swapEntries(mobile, i, j);
                    swapEntries(email, i, j);
                }
            }
        }
    }//End of sortZtoA
}
// End of AddressBook
