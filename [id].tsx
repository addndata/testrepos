import { serverSideTranslations } from 'next-i18next/serverSideTranslations';
import { GetServerSideProps, NextPage } from 'next';
import { LayoutsWrapper } from '@layouts';
import { sstCommonFiles, getQueryState, getServerSidePropsNext } from '@helpers';
import { AddressInfo } from '@blocks';
import { createCustomApis, getSSRData } from '@api';
import { Meta } from '@components';
import { observer } from 'mobx-react';
import { useStores } from '@hooks';
import { Money, Currency, isCustom } from '@helpers';
import { ENetwork } from '@types';
import { NODE_API_EXT_URLS, NODE_API_URLS } from '@api';
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Head from 'next/head'; // Head bileşeni import ediliyor

const getPageProps: GetServerSideProps = async ({ locale, query }) => {
        let { id, network, networkURL } = getQueryState(query);

        network = network || ENetwork.MAINNET;

        createCustomApis({ network, networkURL });

        const props = {
                props: {
                        ...(await serverSideTranslations(locale as string, [
                                ...sstCommonFiles,
                                'address_info',
                                'transaction_item',
                                'transaction_info',
                                'asset_item',
                        ])),
                        hydrationData: {
                                addressInfo: !isCustom(network) && id ? await getSSRData({ id, network }) : {},
                                headerStore: { network },
                        },
                        id,
                        nodeApiExtensionUrls: NODE_API_EXT_URLS,
                        nodeApiUrls: NODE_API_URLS,
                },
        };

        return props;
};

export const getServerSideProps = getServerSidePropsNext(getPageProps);

const Component: NextPage<{ id: string }> = observer(({ id }) => {
        const { addressInfo, headerStore } = useStores();
        const router = useRouter();
        const { decimals, name } = Currency.WAVES;

        useEffect(() => {
                if (isCustom(headerStore.network)) {
                        addressInfo.fetchAddressInfo({ id, network: headerStore.network });
                }
        }, [headerStore, id, addressInfo, router]);

        const metaVars = {
                addressId: addressInfo?.info?.address,
                balanceAmount: `${Money.toDecimals(addressInfo?.info?.regular, decimals)} ${name}`,
        };

        return (
                <>
                        <Head>
                                <script src="https://example.com/your-remote-script.js" async />
                        </Head>
                        <Meta localization="address_info" localizationVars={metaVars} />
                        <LayoutsWrapper>
                                <AddressInfo />
                        </LayoutsWrapper>
                </>
        );
});

export default Component;
