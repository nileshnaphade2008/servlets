package com.nilesh.servlet;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

	private LoginController servlet;
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private HttpSession session;
	
	@Mock
	private RequestDispatcher dispatcher;

	@Before
	public void setUp() {
		servlet = new LoginController();
	}

	@Test
	public void testDoPostWithValidCredentials() throws Exception {
		// Setup
		when(request.getParameter("uname")).thenReturn("nilesh");
		when(request.getSession(true)).thenReturn(session);
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);

		// Execute
		servlet.doPost(request, response);

		// Verify
		verify(session).setAttribute("user", "nilesh");
		verify(session).setMaxInactiveInterval(60);
		verify(response).sendRedirect("home.jsp");
		verify(response).setContentType("text/html");
	}

	@Test
	public void testDoPostWithInvalidCredentials() throws Exception {
		// Setup
		when(request.getParameter("uname")).thenReturn("invalid");
		when(request.getSession(true)).thenReturn(session);
		when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);

		// Execute
		servlet.doPost(request, response);

		// Verify
		verify(dispatcher).include(request, response);
		verify(response).setContentType("text/html");
	}

	@Test
	public void testDoPostWithDifferentCase() throws Exception {
		// Setup - test case insensitivity
		when(request.getParameter("uname")).thenReturn("NILESH");
		when(request.getSession(true)).thenReturn(session);
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);

		// Execute
		servlet.doPost(request, response);

		// Verify
		verify(session).setAttribute("user", "NILESH");
		verify(response).sendRedirect("home.jsp");
	}
}
