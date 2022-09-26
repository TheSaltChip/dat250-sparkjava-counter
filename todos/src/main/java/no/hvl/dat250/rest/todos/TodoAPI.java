package no.hvl.dat250.rest.todos;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import static spark.Spark.*;

/**
 * Rest-Endpoint.
 */
public class TodoAPI {

    private static final List<Todo> todos = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong(0);

    private static final Pattern notDigit = Pattern.compile("^\\D*$");
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        after((req, res) -> res.type("application/json"));

        get("/todos", (req, resp) -> gson.toJson(todos));

        get("/todos/:id", (req, resp) -> {
            String param = req.params(":id");

            if (isNotANumber(param)) {
                resp.body("The id \"" + param + "\" is not a number!");
                return resp;
            }

            Todo todo = getTodo(param);

            resp.body(todo == null
                    ? "Todo with the id  \"" + param + "\" not found!"
                    : gson.toJson(todo));

            return resp;
        });

        post("/todos", (req, resp) -> {
            Todo t = gson.fromJson(req.body(), Todo.class);

            Todo t1 = new Todo(idCounter.getAndAdd(1L), t.getSummary(), t.getDescription());
            todos.add(t1);
            resp.body(gson.toJson(t1));

            return resp;
        });

        put("/todos/:id", (req, resp) -> {
            String param = req.params(":id");

            if (isNotANumber(param)) {
                resp.body("The id \"" + param + "\" is not a number!");
                return resp;
            }

            Todo todo = getTodo(param);

            if (todo == null) {
                resp.body("Todo with the id  \"" + param + "\" not found!");
                return resp;
            }

            todos.remove(todo);

            Todo newTodo = gson.fromJson(req.body(), Todo.class);

            todos.add(newTodo);

            resp.body(gson.toJson(newTodo));

            return resp;
        });

        delete("/todos/:id", (req, resp) -> {
            String param = req.params(":id");

            if(isNotANumber(param)){
                resp.body("The id \""+ param +"\" is not a number!");
                return resp;
            }

            Todo todo = getTodo(param);

            if(todo == null){
                resp.body("Todo with the id  \"" + param + "\" not found!");
                return resp;
            }

            todos.remove(todo);

            resp.body(gson.toJson(todo));
            return resp;
        });
    }

    private static Todo getTodo(String id){
        return todos.stream()
                .filter(t -> t.getId().toString().equals(id))
                .findAny()
                .orElse(null);
    }

    private static boolean isNotANumber(String string){
        return notDigit.matcher(string).matches();
    }

}
