package com.codeup.adlister.controllers;

import com.codeup.adlister.dao.DaoFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "controllers.ViewAdServlet", urlPatterns = "/ads/view")
public class ViewAdServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = (String) request.getSession().getAttribute("viewId");
        request.setAttribute("ads", DaoFactory.getAdsDao().findId(searchTerm));

        request.getRequestDispatcher("/WEB-INF/ads/viewAd.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String deleteId = request.getParameter("deleteId");
        String edit = request.getParameter("edit");
        if (deleteId != null) {
            DaoFactory.getAdsDao().delete(Long.parseLong(deleteId));
            response.sendRedirect("/ads");
        }
        if (edit != null) {
            request.setAttribute("ads", DaoFactory.getAdsDao().findId("2"));
            request.getSession().setAttribute("edit", 2);
            response.sendRedirect("/ads/edit");
        }

    }

}

