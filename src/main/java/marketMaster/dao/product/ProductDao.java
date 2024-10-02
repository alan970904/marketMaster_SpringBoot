package marketMaster.dao.product;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import marketMaster.bean.product.ProductBean;

@Repository
@Transactional
public class ProductDao {
	@Autowired
	private SessionFactory factory;

	public ProductBean insertProduct(ProductBean product) {
		Session session = factory.getCurrentSession();
		if (product != null) {
			session.persist(product);
			return product;
		}
		return null;
	}

	public ProductBean getOne(String productId) {
		Session session = factory.getCurrentSession();

		ProductBean product = session.get(ProductBean.class, productId);
		if (product != null) {
			return product;
		}
		return null;
	}

	public List<ProductBean> getAll() {
		Session session = factory.getCurrentSession();

		Query<ProductBean> query = session.createQuery("from ProductBean", ProductBean.class);
		return query.list();
	}

	public ProductBean updateProduct(ProductBean product) {
		Session session = factory.getCurrentSession();

		String productId = product.getProductId();
		String productName = product.getProductName();
		int productPrice = product.getProductPrice();
		int productSafeInventory = product.getproductSafeInventory();

		ProductBean productBean = session.get(ProductBean.class, productId);
		if (productBean != null) {
			productBean.setProductName(productName);
			productBean.setproductSafeInventory(productSafeInventory);
			productBean.setProductPrice(productPrice);
			session.merge(productBean);
			return productBean;
		}
		return null;
	}

	public ProductBean shelveProduct(String productId, int shelveNumber) {
		Session session = factory.getCurrentSession();

		ProductBean productBean = session.get(ProductBean.class, productId);
		int inventory = productBean.getNumberOfInventory();
		int shelve = productBean.getNumberOfShelve();
		if (productBean != null) {
			productBean.setNumberOfInventory(inventory - shelveNumber);
			productBean.setNumberOfShelve(shelve + shelveNumber);
			session.merge(productBean);
			return productBean;
		}
		return null;
	}

	public ProductBean removeProduct(String productId) {
		Session session = factory.getCurrentSession();

		ProductBean productBean = session.get(ProductBean.class, productId);
		if (productBean != null) {
			int inventory = productBean.getNumberOfInventory();
			int shelve = productBean.getNumberOfShelve();
			int remove = productBean.getNumberOfRemove();
			productBean.setNumberOfInventory(0);
			productBean.setNumberOfShelve(0);
			productBean.setNumberOfRemove(remove + shelve + inventory);
			session.merge(productBean);
		}
		return productBean;
	}

}