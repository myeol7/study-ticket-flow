package com.example.ticketflow.interfaces.interceptor;

import com.example.ticketflow.domain.Idempotency.IdempotencyHistory;
import com.example.ticketflow.domain.Idempotency.IdempotencyHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyHistoryRepository idempotencyHistoryRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String idempotencyKey = request.getHeader("Idempotency-Key");
            if (idempotencyKey == null || idempotencyKey.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Idempotency-Key 헤더가 누락되었습니다.");
                return false;
            }

            try {
                idempotencyHistoryRepository.save(new IdempotencyHistory(idempotencyKey));
            } catch (DataIntegrityViolationException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("중복된 요청입니다. 잠시 후 다시 확인해주세요.");
                return false;
            }
        }

        return true; // 요청이 유효하면 다음 인터셉터나 컨트롤러로 진행
    }
}
