package spacerent;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="spaces", path="spaces")
public interface SpaceRepository extends PagingAndSortingRepository<Space, Long>{

    Space findByBookid(Long bookid);
  
}
