import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/add")
public class AddServlet extends HttpServlet {

    public void doPost(HttpServletRequest req,
                       HttpServletResponse res)
                       throws IOException {

        int a = Integer.parseInt(req.getParameter("a"));
        int b = Integer.parseInt(req.getParameter("b"));

        int total = a + b;

        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        out.println("<h2>Total = " + total + "</h2>");
    }
}