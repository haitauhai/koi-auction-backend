
package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<?> addOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            Order order = orderService.addOrder(orderRequest.getAuctionID(), orderRequest, userId);
            if(order != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse("Add order successful"));
            }else {
                return ResponseEntity.ok(new StringResponse("Add order failed"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable Integer orderId,
            @RequestBody OrderRequest orderRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            Order updatedOrder = orderService.updateOrder(orderId, orderRequest, userId);
            return ResponseEntity.ok(new StringResponse("Order updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }


    @GetMapping()
    public List<OrderResponse> getOrdersByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return orderService.getOrdersByUser(userId);
    }
    @GetMapping("/admin")
    public List<OrderResponse> getOrderAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        User user=userPrinciple.getUser();
        if(!user.getRole().equals("Admin")){
            throw new RuntimeException("User is not authenticated");
        }
        return orderService.getAllOrder();
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Integer orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        try {
            OrderResponse orderResponse = orderService.getOrderById(orderId, userId);
            return ResponseEntity.ok(orderResponse);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/{orderId}/shipping")
    public ResponseEntity<?> updateStatusToShipping(@PathVariable Integer orderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            return ResponseEntity.ok(orderService.changeStatusToShipping(orderId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/dispute")
    public ResponseEntity<?> updateStatusToDispute(@PathVariable Integer orderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            return ResponseEntity.ok(orderService.changeStatusToDispute(orderId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/done")
    public ResponseEntity<?> markOrderAsDone(@PathVariable Integer orderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            return ResponseEntity.ok(orderService.doneOrder(orderId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }
    @GetMapping("/dispute")
    public ResponseEntity<?> listOrder() {
        try {
            // Kiểm tra người dùng đã xác thực chưa
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            String userRole = userPrinciple.getUser().getRole();

            // Kiểm tra quyền truy cập của vai trò
            if (!userRole.equals("Staff") && !userRole.equals("Admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: only Staff or Admin allowed");
            }

            // Trả về kết quả của các đơn hàng có trạng thái "Dispute"
            return ResponseEntity.ok(orderService.findOrderByStatus());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }

    @PostMapping("/dispute")
    public ResponseEntity<?> handleDisputeOrder(
            @RequestParam Integer orderId,  // Chuyển từ @PathVariable sang @RequestParam
            @RequestParam String action
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            String userRole = userPrinciple.getUser().getRole();

            if (!userRole.equals("Staff") && !userRole.equals("Admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: only Staff or Admin allowed");
            }

            if ("reject".equalsIgnoreCase(action)) {
                return ResponseEntity.ok(orderService.doneOrder(orderId, userPrinciple.getId()));
            } else if ("approve".equalsIgnoreCase(action)) {
                return ResponseEntity.ok(orderService.rejectOrder(orderId, userPrinciple.getId()));
            } else {
                return ResponseEntity.badRequest().body("Invalid action. Use 'approve' or 'reject'.");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }
}

