            package marketMaster.bean.restock;

            import jakarta.persistence.*;
            import lombok.AllArgsConstructor;
            import lombok.Getter;
            import lombok.NoArgsConstructor;
            import lombok.Setter;
            import marketMaster.bean.employee.EmpBean;

            import java.time.LocalDate;
            import java.util.Set;
            @Getter
            @Setter
            @AllArgsConstructor
            @NoArgsConstructor
            @Entity
            @Table(name = "restocks")
            public class RestocksBean {

                @Id
                @Column(name = "restock_id")
                private String restockId;

                @Column(name = "restock_total_price")
                private int restockTotalPrice;

                @Column(name = "restock_date")
                private LocalDate restockDate;

                @ManyToOne(fetch = FetchType.LAZY)
                @JoinColumn(name = "employee_id")
                private EmpBean employee;

                // 與 RestockDetailsBean 的一對多關係
                @OneToMany(mappedBy = "restock", cascade = CascadeType.ALL)
                private Set<RestockDetailsBean> restockDetails;


                @Override
                public String toString() {
                    return "RestocksBean{" +
                            "restockId='" + restockId + '\'' +
                            ", restockTotalPrice=" + restockTotalPrice +
                            ", restockDate=" + restockDate +
                            ", employee=" + employee +
                            ", restockDetails=" + restockDetails +
                            '}';
                }
            }
