package alfred.command;

import alfred.exceptions.DuplicationException;
import alfred.exceptions.InvalidDateTimeException;
import alfred.exceptions.InvalidInputException;
import alfred.storage.AlfredStorage;
import alfred.task.Event;
import alfred.task.Task;
import alfred.ui.AlfredUserInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

/**
 * Encapsulates the Event Command.
 */
public class EventCommand extends Command {
    private String[] arguments;

    /**
     * Constructs an EventCommand object. Assumes that first word is
     * "event", and splits input from console accordingly to get
     * other arguments.
     *
     * @param input String input from the console. Assumes that input
     *              begins with "event".
     */
    public EventCommand(String input) {
        this.arguments = input.substring(5).split(" /at ");
        this.arguments = Arrays.stream(this.arguments).filter(in -> !in.isEmpty())
                .toArray(String[]::new); // filter away empty strings
    }

    /**
     * Executes the event command. Checks for valid input, modifies
     * the internal data state of Alfred, then prints new task to console.
     *
     * @param ui      AlfredUserInterface object used as the UI interface for Alfred.
     * @param storage AlfredStorage object used to handle internal storage for Alfred.
     * @throws InvalidDateTimeException If date-time being given is not in ISO format.
     * @throws InvalidInputException    If any missing arguments.
     * @throws DuplicationException     If the task is duplicated.
     */
    @Override
    public void execute(AlfredUserInterface ui, AlfredStorage storage)
            throws InvalidDateTimeException, InvalidInputException,
            DuplicationException {
        String response = this.response(ui, storage);
        ui.sandwichAndPrint(response);
    }

    /**
     * Executes the event command. Checks for valid input, modifies
     * the internal data state of Alfred, then returns String output for user.
     *
     * @param ui      AlfredUserInterface object used as the UI interface for Alfred.
     * @param storage AlfredStorage object used to handle internal storage for Alfred.
     * @return String output meant for user.
     * @throws InvalidDateTimeException If date-time being given is not in ISO format.
     * @throws InvalidInputException    If any missing arguments.
     * @throws DuplicationException     If the task is duplicated.
     */
    public String response(AlfredUserInterface ui, AlfredStorage storage)
            throws InvalidDateTimeException, InvalidInputException,
            DuplicationException {

        assert this.arguments != null;

        // check that only description and date
        if (this.arguments.length != 2) {
            throw new InvalidInputException();
        }

        // check that second argument is a valid date format
        try {
            LocalDateTime.parse(this.arguments[1]);
        } catch (DateTimeParseException dte) {
            throw new InvalidDateTimeException();
        }

        // modify data state
        Task event = new Event(this.arguments[0], this.arguments[1]);
        storage.addTask(event);

        // response
        String out = "Yes sir, I've added this task.\n";
        out += event.toString() + "\n";
        out += storage.summarizeList();
        return out;
    }

    /**
     * Returns True if command is an Exit Command.
     *
     * @return False
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
