package com.project.camping;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.dao.CampingDAO;
import com.project.vo.CampDataVO;
import com.project.vo.CampingList;
import com.project.vo.CampingManagerVO;
import com.project.vo.CampingVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	
	@Autowired
	private SqlSession sqlSession;
	
	@RequestMapping("/")
	public String home(HttpServletRequest request, Model model) {
		return "redirect:mainpage";
	}
	
	@RequestMapping("/mainpage")
	public String mainpage(HttpServletRequest request, Model model) {
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		int totalCamp = dao.seletCount();
		model.addAttribute("totalCamp", totalCamp);
		
		ArrayList<String> cmapDataList = dao.selectDataList(); 
		
		model.addAttribute("cmapDataList", cmapDataList);
		return "mainpage";
	}
	
	/**
	 * 관리자 로그인 페이지 연결
	 */
	@RequestMapping("/loginMain")
	public String loginMain(HttpServletRequest request, Model model) {
		return "loginMain";
	}
	/**
	 * 관리자 로그인 확인
	 */
	@RequestMapping("/loginOK")
	public String loginOK(HttpServletRequest request, Model model) {
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		HashMap<String, String> hmap = new HashMap<String, String>();
		hmap.put("id", id);
		hmap.put("pw", pw);
		int check = dao.loginCheck(hmap);
		
		if(check > 0) {
			HttpSession session = request.getSession();
//			세션 변수 저장 => 세션이 저장
			session.setAttribute("manager", "true");
		}else {
			return "redirect:loginMain";
		}
		return "redirect:mainpage";
	}
	
	
	/**
	 * @param request
	 * @param model
	 * @return
	 * 댓글 추가하는 메소드
	 */
	@RequestMapping("/insert")
	public String insert(HttpServletRequest request, Model model) {
		
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
		CampingVO campingVO = ctx.getBean("vo", CampingVO.class);
		campingVO.setName(request.getParameter("name"));
		campingVO.setContent(request.getParameter("content"));
		campingVO.setPw(request.getParameter("pw"));
		campingVO.setCampNumber(Integer.parseInt(request.getParameter("campNumber")));
		model.addAttribute("campNumber", campingVO.getCampNumber());
		dao.insert(campingVO);
		ctx.close();
		return "redirect:list2";
	}
	
	
	/**
	 * @param request
	 * @param model
	 * @return
	 * 선택한 캠핑장의 댓글을 불러와주는 메소드
	 */
	@RequestMapping("/list")
	public String list(HttpServletRequest request, Model model) {
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		
//		=================================================댓글
		int pageSize = 10;
		int currentPage = 1;
		int campNumber = 1; // 캠핑장 구분 index
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		int totalCount =  dao.selectCount(campNumber);
		CampingList campingList = ctx.getBean("list", CampingList.class);
	
		campingList.initMvcboardList(pageSize, totalCount, currentPage);
		
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap.put("startNo", campingList.getStartNo());
		hmap.put("endNo", campingList.getEndNo());
		hmap.put("campNumber", campNumber);
		
		campingList.setList(dao.selectList(hmap));
		model.addAttribute(campingList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("campNumber", campNumber);
		
//		=================================================캠핑장 자료
		CampDataVO campDataVO = dao.selectCamp(campNumber);
		model.addAttribute("campDataVO", campDataVO);
		model.addAttribute("enter", "\r\n");
		
		ctx.close();
		return "list";
	}
	
	/**
	 * @param request
	 * @param model
	 * @return
	 * 댓글 페이지
	 * 
	 */
	@RequestMapping("/list2")
	public String list2(HttpServletRequest request, Model model) {
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		int pageSize = 10;
		int currentPage = 1;
		int campNumber = 0; // 캠핑장 구분 index
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		int totalCount =  dao.selectCount(campNumber);
		CampingList campingList = ctx.getBean("list", CampingList.class);
	
		campingList.initMvcboardList(pageSize, totalCount, currentPage);
		
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap.put("startNo", campingList.getStartNo());
		hmap.put("endNo", campingList.getEndNo());
		hmap.put("campNumber", campNumber);
		
		campingList.setList(dao.selectList(hmap));
		model.addAttribute(campingList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("campNumber", campNumber);
		ctx.close();
		return "list2";
	}
	/**
	 * @param request
	 * @param model
	 * @return
	 * 댓글 페이지
	 * 
	 */
	@RequestMapping("/list4")
	public String list4(HttpServletRequest request, Model model) {
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		
//		=================================================댓글
		int campNumber = 1; // 캠핑장 구분 index
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
		} catch (Exception e) {
		}
		
		model.addAttribute("campNumber", campNumber);
		
//		=================================================캠핑장 자료
		CampDataVO campDataVO = dao.selectCamp(campNumber);
		model.addAttribute("campDataVO", campDataVO);
		model.addAttribute("enter", "\r\n");
		
		ctx.close();
		return "list4";
	}
	
	/**
	 * @param request
	 * @param model
	 * @return
	 * 지도보기 누르면 지도를 보여주는 기능
	 */
	@RequestMapping("/list3")
	public String list3(HttpServletRequest request, Model model) {
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		
//		=================================================댓글
		int pageSize = 10;
		int currentPage = 1;
		int campNumber = 1; // 캠핑장 구분 index
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		int totalCount =  dao.selectCount(campNumber);
		CampingList campingList = ctx.getBean("list", CampingList.class);
		
		campingList.initMvcboardList(pageSize, totalCount, currentPage);
		
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap.put("startNo", campingList.getStartNo());
		hmap.put("endNo", campingList.getEndNo());
		hmap.put("campNumber", campNumber);
		
		campingList.setList(dao.selectList(hmap));
		model.addAttribute(campingList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("campNumber", campNumber);
		
//		=================================================캠핑장 자료
		CampDataVO campDataVO = dao.selectCamp(campNumber);
		model.addAttribute("campDataVO", campDataVO);
		
		ctx.close();
		return "list3";
	}
	
	@RequestMapping("/list5")
	public String list5(HttpServletRequest request, Model model) {
		return "list5";
	}
	
	/**
	 * @param request
	 * @param model
	 * @return
	 * 댓글에 좋아요를 클릭하면 좋아요 횟수를 올려주는 메소드
	 */
	@RequestMapping("/up")
	public String up(HttpServletRequest request, Model model) {
//		System.out.println("컨트롤러의 increment() 메소드");
	
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		
		int currentPage = 1;
		int campNumber = 0;
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
//		request 객체로 넘어온 조회수를 증가시킬 글번호를 받는다.		
		int idx = Integer.parseInt(request.getParameter("idx"));
//		조회수를 증가시키는 메소드를 실행한다.
		dao.up(idx);

		model.addAttribute("idx", idx);
		model.addAttribute("campNumber", campNumber);
		model.addAttribute("currentPage", currentPage);
		return "redirect:list2";
	}
	/**
	 * @param request
	 * @param model
	 * @return
	 * 댓글에 싫어요 클릭하면 좋아요 횟수를 올려주는 메소드
	 */
	@RequestMapping("/down")
	public String down(HttpServletRequest request, Model model) {
	
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		
		int currentPage = 1;
		int campNumber = 0;
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		
//		request 객체로 넘어온 조회수를 증가시킬 글번호를 받는다.		
		int idx = Integer.parseInt(request.getParameter("idx"));
//		조회수를 증가시키는 메소드를 실행한다.
		dao.down(idx);
		
		model.addAttribute("idx", idx);
		model.addAttribute("campNumber", campNumber);
		model.addAttribute("currentPage", currentPage);
		
		return "redirect:list2";
	}
	
	
	@RequestMapping("/update")
	public String update(HttpServletRequest request, Model model, CampingVO campingVO) {
		int currentPage = 1;
		int campNumber = 0;
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		dao.update(campingVO);
		
//		글 수정 작업 후 돌아갈 페이지 번호를 model 객체에 저장한다.
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("campNumber", campNumber);
		
		return "redirect:list2";
	}
	
	/**
	 * @param request
	 * @param model
	 * @param campingVO
	 * @return
	 * 관리자가 게시글을 삭제
	 */
	@RequestMapping("/delete")
	public String delete(HttpServletRequest request, Model model) {
		int currentPage = 1;
		int campNumber = 0;
		try {
			campNumber = Integer.parseInt(request.getParameter("campNumber"));
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
		
		int idx = Integer.parseInt(request.getParameter("idx"));
		dao.delete(idx);
//		글 수정 작업 후 돌아갈 페이지 번호를 model 객체에 저장한다.
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("campNumber", campNumber);
		
		return "redirect:list2";
	}
	
	@RequestMapping("/contentView")
	public String contentView(HttpServletRequest request, Model model) {
		CampingDAO dao = sqlSession.getMapper(CampingDAO.class);

		int idx = Integer.parseInt(request.getParameter("idx"));

		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
		CampingVO campingVO = ctx.getBean("vo", CampingVO.class);
		campingVO = dao.selectByIdx(idx);

		model.addAttribute("vo", campingVO);
		model.addAttribute("currentPage", Integer.parseInt(request.getParameter("currentPage")));
		
		return "contentView";
	}
	
   
   /**
 * @param request
 * @param model
 * @return
 * 관리자로 로그인 하면 관리자를 등록할 수 있는 기능
 */
@RequestMapping("/register")
   public String register(HttpServletRequest request, Model model) {
	   return "register";
   }
/**
 * @param request
 * @param model
 * @return
 * 관리자로 로그인 하면 관리자를 등록할 수 있는 기능
 */
@RequestMapping("/insertManager")
public String insertManager(HttpServletRequest request, Model model) {
	GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:application_ctx.xml");
	CampingManagerVO managerVO= ctx.getBean(CampingManagerVO.class);
	managerVO.setId(request.getParameter("id"));
	managerVO.setPw(request.getParameter("pw"));
	CampingDAO dao = sqlSession.getMapper(CampingDAO.class);
	dao.register(managerVO);
	return "redirect:mainpage";
}
/**
* @param request
* @param model
* @return
* 관리자로 로그인 하면 관리자를 등록할 수 있는 기능
*/ 
@RequestMapping("/logout")
public String logout(HttpServletRequest request, Model model) {
	HttpSession session = request.getSession();
	session.invalidate();
	return "redirect:mainpage";
}
   
   /**
 * @param request
 * @param model
 * @return
 * 지도 보여주는 페이지로 이동
 */
@RequestMapping("/path")
   public String path(HttpServletRequest request, Model model) {
	   return "path";
   }
	
}
