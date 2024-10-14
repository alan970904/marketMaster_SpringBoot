package marketMaster.service.bonus;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;
import marketMaster.bean.customer.CustomerBean;
import marketMaster.service.customer.CustomerRepository;

@Repository
public class PointsHistoryCustomerRepositoryImpl implements PointsHistoryCustomerRepository {

    @Autowired
    private CustomerRepository customerRepository;

    // 自定義方法
    @Override
    public Optional<CustomerBean> findByCustomerTel(String customerTel) {
        return customerRepository.findById(customerTel);
    }

    @Override
    public Optional<String> findCustomerNameByTel(String customerTel) {
        return findByCustomerTel(customerTel)
                .map(CustomerBean::getCustomerName);
    }

    // JpaRepository 方法
    @Override
    public List<CustomerBean> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public List<CustomerBean> findAll(Sort sort) {
        return customerRepository.findAll(sort);
    }

    @Override
    public List<CustomerBean> findAllById(Iterable<String> ids) {
        return customerRepository.findAllById(ids);
    }

    @Override
    public <S extends CustomerBean> List<S> saveAll(Iterable<S> entities) {
        return customerRepository.saveAll(entities);
    }

    @Override
    public void flush() {
        customerRepository.flush();
    }

    @Override
    public <S extends CustomerBean> S saveAndFlush(S entity) {
        return customerRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends CustomerBean> List<S> saveAllAndFlush(Iterable<S> entities) {
        return customerRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<CustomerBean> entities) {
        customerRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> ids) {
        customerRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() {
        customerRepository.deleteAllInBatch();
    }

    @Override
    public CustomerBean getOne(String id) {
        return customerRepository.getOne(id);
    }

    @Override
    public CustomerBean getById(String id) {
        return customerRepository.getById(id);
    }

    @Override
    public CustomerBean getReferenceById(String id) {
        return customerRepository.getReferenceById(id);
    }

    @Override
    public <S extends CustomerBean> List<S> findAll(Example<S> example) {
        return customerRepository.findAll(example);
    }

    @Override
    public <S extends CustomerBean> List<S> findAll(Example<S> example, Sort sort) {
        return customerRepository.findAll(example, sort);
    }

    @Override
    public Page<CustomerBean> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public <S extends CustomerBean> S save(S entity) {
        return customerRepository.save(entity);
    }

    @Override
    public Optional<CustomerBean> findById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return customerRepository.existsById(id);
    }

    @Override
    public long count() {
        return customerRepository.count();
    }

    @Override
    public void deleteById(String id) {
        customerRepository.deleteById(id);
    }

    @Override
    public void delete(CustomerBean entity) {
        customerRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        customerRepository.deleteAllById(ids);
    }

    @Override
    public void deleteAll(Iterable<? extends CustomerBean> entities) {
        customerRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        customerRepository.deleteAll();
    }

    @Override
    public <S extends CustomerBean> Optional<S> findOne(Example<S> example) {
        return customerRepository.findOne(example);
    }

    @Override
    public <S extends CustomerBean> Page<S> findAll(Example<S> example, Pageable pageable) {
        return customerRepository.findAll(example, pageable);
    }

    @Override
    public <S extends CustomerBean> long count(Example<S> example) {
        return customerRepository.count(example);
    }

    @Override
    public <S extends CustomerBean> boolean exists(Example<S> example) {
        return customerRepository.exists(example);
    }

    @Override
    public <S extends CustomerBean, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return customerRepository.findBy(example, queryFunction);
    }
}