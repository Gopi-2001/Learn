package in.gopikant.billingSoftware.repository;

import in.gopikant.billingSoftware.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemEntityRepository extends JpaRepository<OrderEntity, Long> {

}
