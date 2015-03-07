/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Servlets;

import Credentials.credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0628439
 */
@WebServlet("/Products")
public class Products extends HttpServlet{
   
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                // There are no parameters at all
                out.println(getResultsARRAYZ("SELECT * FROM Products"));
            } else {
                // There are some parameters
                int id = Integer.parseInt(request.getParameter("id"));
                out.println(getResults("SELECT * FROM Products WHERE ProductID = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("{ \"ProductID\" : %s, \"Name\" : %s, \"Description\" : %s, \"Quantity\" : %s }\n", rs.getInt("ProductID"), rs.getString("Name"), rs.getString("Description"),rs.getInt("Quantity")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    private String getResultsARRAYZ(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            sb.append("[ ");
            while (rs.next()) {
                sb.append(String.format("{ \"ProductID\" : %s, \"Name\" : %s, \"Description\" : %s, \"Quantity\" : %s },\n", rs.getInt("ProductID"), rs.getString("Name"), rs.getString("Description"),rs.getInt("Quantity")));
            }
            sb.setLength(Math.max(sb.length() - 2, 0));
            sb.append(" ]");
        } catch (SQLException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String result;
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                // There are some parameters                
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                String quantity = request.getParameter("quantity");
               result = (doUpdate("INSERT INTO Products (Name, Description, Quantity) VALUES (?, ?, ?)", name, desc, quantity));
               if (result.equalsIgnoreCase("YOU MESSED UP")){
                    response.setStatus(500);
                }
                out.println(result);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /Products?name=XXX&description=XXX&quantity=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String doUpdate(String query, String... params) {
        String STRINGZ = "";
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
           pstmt.executeUpdate();
           
           ResultSet keys = pstmt.getGeneratedKeys();
           if (keys.next()) {
               STRINGZ = ("<a>http//localhost/assign-3/Products/"+keys.getInt(1) + "<a>");
           }else if (params.length ==4) {
               STRINGZ = ("<a>http//localhost/assign-3/Products/" + String.valueOf(params[4 - 1]) + "<a>");
           }
        } catch (SQLException ex) {
            STRINGZ = "YOU MESSED UP";
        }
        return STRINGZ;
    }
     @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        String result;
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity") && keySet.contains("id")) {
                // There are some parameters                
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                String id = request.getParameter("id");
                result= (doUpdate("UPDATE Products SET Name = ?, Description = ?, Quantity = ? WHERE ProductID = ?", name, desc, quantity, id)) ;
                if (result.equalsIgnoreCase("YOU MESSED UP")){
                    response.setStatus(500);
                }
                out.println(result);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /Products?name=XXX&description=XXX&quantity=XXX&id=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    /**
     *
     * @param request
     * @param response
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String result;
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id")) {
                // There are some parameters                
                String id = request.getParameter("id");
                result = gottaDelete("DELETE FROM Products WHERE ProductID = ?", id) ;
                if (result.equalsIgnoreCase("YOU MESSED UP")){
                    response.setStatus(500);
                }
                out.println(result);
            }
        } catch (IOException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    private String gottaDelete(String query, String... params) {
        String badStuff = "";
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            badStuff = "YOU MESSED UP";
        }
        return badStuff;
    }
}