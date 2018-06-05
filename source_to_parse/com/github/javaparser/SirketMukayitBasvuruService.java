
public interface SirketMukayitBasvuruService {

	/**
	 * Test
	 * @param sirket
	 */
	default List<SirketMukayit> mukayiteAtananlariListele(Kullanici kullanici) {
		return sirketMukayitService.mukayiteAtananlariListele(kullanici);
	}
}