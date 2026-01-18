package com.nilesh.servlet;

import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RestJSessionFilterTest {

	private RestJSessionFilter filter;
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private ServletResponse response;
	
	@Mock
	private FilterChain chain;
	
	@Mock
	private FilterConfig filterConfig;
	
	@Mock
	private HttpSession session;

	@Before
	public void setUp() throws Exception {
		filter = new RestJSessionFilter();
		filter.init(filterConfig);
	}

	@Test
	public void testDoFilterWithValidJSessionIDCookie() throws Exception {
		// Setup
		Cookie cookie = new Cookie("JSESSIONID", "ABC123DEF456");
		Cookie[] cookies = { cookie };
		
		when(request.getCookies()).thenReturn(cookies);
		when(request.getSession()).thenReturn(session);
		when(request.getSession(true)).thenReturn(session);

		// Execute
		filter.doFilter(request, response, chain);

		// Verify
		verify(session).setAttribute("JSESSIONID", "ABC123DEF456");
		verify(chain).doFilter(request, response);
	}

	@Test
	public void testDoFilterWithoutJSessionIDCookie() throws Exception {
		// Setup
		Cookie cookie = new Cookie("OTHER_COOKIE", "value");
		Cookie[] cookies = { cookie };
		
		when(request.getCookies()).thenReturn(cookies);

		// Execute
		filter.doFilter(request, response, chain);

		// Verify - session should not be set
		verify(request, never()).getSession(true);
		verify(chain).doFilter(request, response);
	}

	@Test
	public void testDoFilterWithNullCookies() throws Exception {
		// Setup
		when(request.getCookies()).thenReturn(null);

		// Execute
		filter.doFilter(request, response, chain);

		// Verify
		verify(request, never()).getSession();
		verify(chain).doFilter(request, response);
	}

	@Test
	public void testDoFilterWithMultipleCookies() throws Exception {
		// Setup
		Cookie cookie1 = new Cookie("OTHER_COOKIE", "value1");
		Cookie cookie2 = new Cookie("JSESSIONID", "SESSION123");
		Cookie cookie3 = new Cookie("ANOTHER_COOKIE", "value3");
		Cookie[] cookies = { cookie1, cookie2, cookie3 };
		
		when(request.getCookies()).thenReturn(cookies);
		when(request.getSession()).thenReturn(session);
		when(request.getSession(true)).thenReturn(session);

		// Execute
		filter.doFilter(request, response, chain);

		// Verify
		verify(session).setAttribute("JSESSIONID", "SESSION123");
		verify(chain).doFilter(request, response);
	}

	@Test
	public void testDoFilterWithEmptyJSessionIDCookie() throws Exception {
		// Setup
		Cookie cookie = new Cookie("JSESSIONID", "");
		Cookie[] cookies = { cookie };
		
		when(request.getCookies()).thenReturn(cookies);

		// Execute
		filter.doFilter(request, response, chain);

		// Verify - empty session ID should not set attribute
		verify(request, never()).getSession(true);
		verify(chain).doFilter(request, response);
	}

	@Test
	public void testDoFilterWithNonHttpRequest() throws Exception {
		// Setup - using ServletRequest instead of HttpServletRequest
		ServletRequest nonHttpRequest = mock(ServletRequest.class);

		// Execute
		filter.doFilter(nonHttpRequest, response, chain);

		// Verify - filter should just pass through
		verify(chain).doFilter(nonHttpRequest, response);
	}

	@Test
	public void testDestroy() {
		// Verify destroy doesn't throw exception
		filter.destroy();
	}
}
